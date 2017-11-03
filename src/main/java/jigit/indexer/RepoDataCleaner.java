package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.APIAdapterFactory;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.SortedSet;

public final class RepoDataCleaner {
    @NotNull
    private final APIAdapterFactory apiAdapterFactory;
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final QueueItemManager queueItemManager;

    public RepoDataCleaner(@NotNull APIAdapterFactory apiAdapterFactory,
                           @NotNull CommitManager commitManager,
                           @NotNull QueueItemManager queueItemManager) {
        this.apiAdapterFactory = apiAdapterFactory;
        this.commitManager = commitManager;
        this.queueItemManager = queueItemManager;
    }

    public void clearRepoData(@NotNull JigitRepo repo) throws InterruptedException, IOException {
        final String repoName = repo.getRepoName();
        try {
            DisabledRepos.instance.markDisabled(repoName);
            final APIAdapter apiAdapter = apiAdapterFactory.getAPIAdapter(repo);
            Thread.sleep(repo.getRequestTimeout() * 2 + repo.getSleepTimeout());
            final SortedSet<String> branches = BranchesStrategyFactory.buildBranchesStrategy(repo, apiAdapter).branches();
            for (String branch : branches) {
                clearRepoData(repo, branch);
            }
            clearRepoData(repo, repo.getDefaultBranch());
        } finally {
            DisabledRepos.instance.markEnabled(repo.getRepoName());
        }
    }

    public void clearRepoData(@NotNull JigitRepo repo, @NotNull String branch) throws InterruptedException {
        final String repoName = repo.getRepoName();
        final boolean wasEnabled = !DisabledRepos.instance.disabled(repoName);
        if (wasEnabled) {
            DisabledRepos.instance.markDisabled(repoName);
            Thread.sleep(repo.getRequestTimeout() * 2 + repo.getSleepTimeout());
        }
        queueItemManager.remove(repoName, branch);
        commitManager.removeCommits(repoName, branch);
        if (wasEnabled) {
            DisabledRepos.instance.markEnabled(repoName);
        }
    }
}
