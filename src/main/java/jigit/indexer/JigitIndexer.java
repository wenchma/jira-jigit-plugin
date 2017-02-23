package jigit.indexer;

import com.atlassian.jira.util.JiraKeyUtils;
import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.indexer.api.*;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class JigitIndexer {
    @NotNull
    private static final Logger LOG = Logger.getLogger(JigitIndexer.class);
    private static final int THREAD_POOL_SIZE = 2;
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final QueueItemManager queueItemManager;
    @NotNull
    private final PersistStrategyFactory persistStrategyFactory;
    @NotNull
    private final APIAdapterFactory apiAdapterFactory;

    public JigitIndexer(@NotNull JigitSettingsManager settingsManager,
                        @NotNull CommitManager commitManager,
                        @NotNull QueueItemManager queueItemManager,
                        @NotNull PersistStrategyFactory persistStrategyFactory,
                        @NotNull APIAdapterFactory apiAdapterFactory) {
        this.settingsManager = settingsManager;
        this.commitManager = commitManager;
        this.queueItemManager = queueItemManager;
        this.persistStrategyFactory = persistStrategyFactory;
        this.apiAdapterFactory = apiAdapterFactory;
    }

    public void execute() {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new JigitThreadFactory());
        final CompletionService<JigitRepo> completionService = new ExecutorCompletionService<>(executorService);
        final Map<String, JigitRepo> jigitRepos = settingsManager.getJigitRepos();
        int futureTasks = 0;

        try {
            for (JigitRepo repo : jigitRepos.values()) {
                if (!repo.isNeedToIndex()) {
                    continue;
                }
                final APIAdapter apiAdapter = apiAdapterFactory.getAPIAdapter(repo);
                if (apiAdapter == null) {
                    continue;
                }
                completionService.submit(new Indexer(apiAdapter, repo));
                futureTasks++;
            }
        } catch (Exception e) {
            LOG.error("JigitIndexer::execute", e);
        }

        try {
            for (int i = 0; i < futureTasks; i++) {
                final Future<JigitRepo> projectCompleted = completionService.take();
                projectCompleted.get();
            }
        } catch (InterruptedException e) {
            LOG.error("JigitIndexer::execute - InterruptedException", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOG.error("JigitIndexer::execute - ExecutionException. Cause: ", e.getCause());
        } finally {
            executorService.shutdown();
        }
    }

    private final class Indexer implements Callable<JigitRepo> {
        @NotNull
        private final APIAdapter apiAdapter;
        @NotNull
        private final JigitRepo repo;
        @NotNull
        private final ForcePushHandler deletingForcePushHandler;

        private Indexer(@NotNull APIAdapter apiAdapter, @NotNull JigitRepo repo) {
            this.repo = repo;
            this.apiAdapter = apiAdapter;
            deletingForcePushHandler = new DeletingForcePushHandler(commitManager, queueItemManager, apiAdapter);
        }

        @NotNull
        @Override
        public JigitRepo call() throws IOException, InterruptedException, ParseException {
            try {
                final Map<String, ForcePushHandler> branchFPHandlers = getBranchForcePushHandlers();
                final String repositoryId = repo.getRepositoryId();
                for (String branch : branchFPHandlers.keySet()) {
                    branchFPHandlers.get(branch).handle(repo, branch);
                    indexRepoBranch(repositoryId, branch);
                }
            } catch (LimitExceededException ignored) {
                LOG.warn("Repository request limit exceeded for " + repo.getRepoName()
                        + ". Next indexing would be started at " + new Date(repo.getSleepTo()));
            }

            return repo;
        }

        private void indexRepoBranch(@NotNull String repositoryId, @NotNull String branch) throws IOException, InterruptedException, ParseException {
            try {
                final String startCommitSha1 = apiAdapter.getHeadCommitSha1(branch);
                if (startCommitSha1 == null) {
                    return;
                }

                LOG.info("Starts indexing repository " + repositoryId + " and branch " + branch +
                        " from head commit " + startCommitSha1);
                indexFromCommit(branch, startCommitSha1);
                LOG.info("Ends indexing repository " + repositoryId + " and branch " + branch);
            } catch (SocketTimeoutException e) {
                LOG.info("SocketTimeoutException when trying to get head commit from "
                        + repositoryId + " branch " + branch, e);
            }
        }

        @NotNull
        private Map<String, ForcePushHandler> getBranchForcePushHandlers() {
            final Map<String, ForcePushHandler> branches = new TreeMap<>();
            branches.put(repo.getDefaultBranch(), ForcePushHandler.DO_NOTHING);
            for (String branch : repo.getBranches()) {
                branches.put(branch, deletingForcePushHandler);
            }
            return branches;
        }

        private void indexFromCommit(@NotNull String branch, @NotNull String startCommitSha1) throws IOException, InterruptedException, ParseException {
            final String repositoryId = repo.getRepositoryId();
            final CommitQueue commitQueue = new CommitQueue(queueItemManager, repo.getRepoName(), branch);
            final int skipCount = commitQueue.size();
            commitQueue.add(startCommitSha1);

            int counter = 0;
            while (!commitQueue.isEmpty()) {
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
                final List<String> issueKeys = JiraKeyUtils.getIssueKeysFromString(commitAdapter.getTitle());

                if (!issueKeys.isEmpty()) {
                    try {
                        commitDiffs = commitAdapter.getCommitDiffs();
                    } catch (SocketTimeoutException e) {
                        LOG.info("SocketTimeoutException when trying to get diff " + commitSha1 +
                                " from " + repositoryId + " branch " + branch, e);
                    }
                }

                final Collection<String> nextCommits = persistStrategyFactory.
                        getStrategy(repo.getRepoName(), commitSha1, counter > skipCount).
                        persist(repo.getRepoName(), branch, commitAdapter, issueKeys, commitDiffs);
                commitQueue.remove();

                commitQueue.addAll(nextCommits);
            }
        }
    }

    private static final class JigitThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "jigit-indexer-" + counter.incrementAndGet());
        }
    }
}
