package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public final class RepoDataCleaner {
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final QueueItemManager queueItemManager;

    public RepoDataCleaner(@NotNull CommitManager commitManager,
                           @NotNull QueueItemManager queueItemManager) {
        this.commitManager = commitManager;
        this.queueItemManager = queueItemManager;
    }

    public void clearRepoData(@NotNull JigitRepo repo) throws InterruptedException {
        final String repoName = repo.getRepoName();
        try {
            DisabledRepos.instance.markDisabled(repoName);
            Thread.sleep(repo.getRequestTimeout() * 2 + repo.getSleepTimeout());
            for (String branch : repo.getBranches()) {
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
