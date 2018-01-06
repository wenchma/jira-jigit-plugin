package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.indexer.repository.RepoInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    public void clearRepoData(@NotNull Collection<RepoInfo> repoInfos) throws InterruptedException {
        for (RepoInfo repoInfo : repoInfos) {
            final String repoName = repoInfo.getRepoName();
            try {
                DisabledRepos.instance.markDisabled(repoName);
                for (String branch : repoInfo.branches()) {
                    clearRepoData(repoInfo, branch);
                }
                clearRepoData(repoInfo, repoInfo.getDefaultBranch());
            } finally {
                DisabledRepos.instance.markEnabled(repoInfo.getRepoName());
            }
        }
    }

    public void clearRepoData(@NotNull RepoInfo repoInfo, @NotNull String branch) throws InterruptedException {
        final String repoName = repoInfo.getRepoName();
        final boolean wasEnabled = !DisabledRepos.instance.disabled(repoName);
        if (wasEnabled) {
            DisabledRepos.instance.markDisabled(repoName);
            Thread.sleep(repoInfo.getRequestTimeout() * 2 + repoInfo.getSleepTimeout());
        }
        queueItemManager.remove(repoName, branch);
        commitManager.removeCommits(repoName, branch);
        if (wasEnabled) {
            DisabledRepos.instance.markEnabled(repoName);
        }
    }
}
