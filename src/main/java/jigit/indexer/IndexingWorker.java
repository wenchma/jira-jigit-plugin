package jigit.indexer;

import jigit.ao.QueueItemManager;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import jigit.indexer.api.LimitExceededException;
import jigit.settings.JigitRepo;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;

final class IndexingWorker implements Callable<JigitRepo> {
    @NotNull
    private static final Logger LOG = Logger.getLogger(JigitIndexer.class);
    @NotNull
    private final APIAdapter apiAdapter;
    @NotNull
    private final JigitRepo repo;
    @NotNull
    private final DeletingForcePushHandler deletingForcePushHandler;
    @NotNull
    private final QueueItemManager queueItemManager;
    @NotNull
    private final PersistStrategyFactory persistStrategyFactory;
    @NotNull
    private final IssueKeysExtractor issueKeysExtractor;

    IndexingWorker(@NotNull APIAdapter apiAdapter,
                   @NotNull JigitRepo repo,
                   @NotNull DeletingForcePushHandler deletingForcePushHandler,
                   @NotNull QueueItemManager queueItemManager,
                   @NotNull PersistStrategyFactory persistStrategyFactory,
                   @NotNull IssueKeysExtractor issueKeysExtractor) {
        this.repo = repo;
        this.apiAdapter = apiAdapter;
        this.deletingForcePushHandler = deletingForcePushHandler;
        this.queueItemManager = queueItemManager;
        this.persistStrategyFactory = persistStrategyFactory;
        this.issueKeysExtractor = issueKeysExtractor;
    }

    @NotNull
    @Override
    public JigitRepo call() throws IOException, InterruptedException, ParseException {
        try {
            final Map<String, ForcePushHandler> branchFPHandlers = getBranchForcePushHandlers();
            final String repositoryId = repo.getRepositoryId();
            for (Map.Entry<String, ForcePushHandler> entry : branchFPHandlers.entrySet()) {
                entry.getValue().handle(repo, entry.getKey());
                indexRepoBranch(repositoryId, entry.getKey());
            }
        } catch (LimitExceededException ignored) {
            LOG.warn("Repository request limit exceeded for " + repo.getRepoName()
                    + ". Next indexing starts after " + new Date(repo.getSleepTo()));
        }

        return repo;
    }

    private void indexRepoBranch(@NotNull String repositoryId, @NotNull String branch) throws IOException, InterruptedException, ParseException {
        try {
            final String startCommitSha1 = apiAdapter.getHeadCommitSha1(branch);
            if (startCommitSha1 == null) {
                return;
            }

            LOG.info("Started indexing repository " + repositoryId + " and branch " + branch +
                    " from head commit " + startCommitSha1);
            indexFromCommit(branch, startCommitSha1);
            LOG.info("Ended indexing repository " + repositoryId + " and branch " + branch);
        } catch (SocketTimeoutException e) {
            LOG.info("SocketTimeoutException when trying to get head commit from "
                    + repositoryId + " branch " + branch, e);
        }
    }

    @NotNull
    private Map<String, ForcePushHandler> getBranchForcePushHandlers() {
        final Map<String, ForcePushHandler> branchHandlers = new LinkedHashMap<>();
        branchHandlers.put(repo.getDefaultBranch(), ForcePushHandler.DO_NOTHING);
        for (String branch : repo.getBranches()) {
            branchHandlers.put(branch, deletingForcePushHandler);
        }
        return branchHandlers;
    }

    private void indexFromCommit(@NotNull String branch, @NotNull String startCommitSha1) throws IOException, InterruptedException, ParseException {
        final String repositoryId = repo.getRepositoryId();
        final String repoName = repo.getRepoName();
        final CommitQueue commitQueue = new CommitQueue(queueItemManager, repoName, branch);
        final int skipCount = commitQueue.size();
        commitQueue.add(startCommitSha1);

        int counter = 0;
        while (!(commitQueue.isEmpty() || DisabledRepos.instance.disabled(repoName))) {
            final long requestsQuantity = apiAdapter.getRequestsQuantity();
            if (requestsQuantity > 0 && repo.getSleepRequests() > 0 && requestsQuantity % repo.getSleepRequests() == 0) {
                Thread.sleep(repo.getSleepTimeout());
            }
            counter++;

            final String commitSha1 = commitQueue.peek();
            if (commitSha1 == null) {
                continue;
            }
            LOG.info("Commit sha1 fetched " + commitSha1);

            final CommitAdapter commitAdapter;
            try {
                commitAdapter = apiAdapter.getCommit(commitSha1);
            } catch (SocketTimeoutException e) {
                LOG.info("SocketTimeoutException when trying to get commit " + commitSha1 +
                        " from " + repositoryId + " branch " + branch, e);
                return;
            }

            Collection<CommitFileAdapter> commitDiffs = Collections.emptyList();
            final Set<String> issueKeys =
                    new HashSet<>(issueKeysExtractor.extract(commitAdapter.getTitle()));

            if (!issueKeys.isEmpty()) {
                try {
                    commitDiffs = commitAdapter.getCommitDiffs();
                } catch (SocketTimeoutException e) {
                    LOG.info("SocketTimeoutException when trying to get diff " + commitSha1 +
                            " from " + repositoryId + " branch " + branch, e);
                }
            }

            final Collection<String> nextCommits = persistStrategyFactory.
                    getStrategy(repoName, commitSha1, counter > skipCount).
                    persist(repoName, branch, commitAdapter, issueKeys, commitDiffs);
            commitQueue.remove();
            commitQueue.addAll(nextCommits);
        }
        if (DisabledRepos.instance.disabled(repoName)) {
            throw new InterruptedException("Indexing interrupted from the outside.");
        }
    }
}
