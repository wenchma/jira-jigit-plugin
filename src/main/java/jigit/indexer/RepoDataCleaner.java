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

    public void clearRepoData(@NotNull JigitRepo repo) {
        for (String branch : repo.getBranches()) {
            clearRepoData(repo.getRepoName(), branch);
        }
        clearRepoData(repo.getRepoName(), repo.getDefaultBranch());
    }

    public void clearRepoData(@NotNull String repoName, @NotNull String branch) {
        queueItemManager.remove(repoName, branch);
        commitManager.removeCommits(repoName, branch);
    }
}
