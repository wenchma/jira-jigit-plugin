package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.entities.Commit;
import jigit.indexer.repository.RepoInfo;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class DeletingForcePushHandler implements ForcePushHandler {
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final RepoInfo repoInfo;
    @NotNull
    private final RepoDataCleaner repoDataCleaner;

    public DeletingForcePushHandler(@NotNull CommitManager commitManager,
                                    @NotNull RepoInfo repoInfo,
                                    @NotNull RepoDataCleaner repoDataCleaner) {
        this.commitManager = commitManager;
        this.repoInfo = repoInfo;
        this.repoDataCleaner = repoDataCleaner;
    }

    @Override
    public void handle(@NotNull String branch) throws IOException, InterruptedException {
        if (wasBranchForcePushed(branch)) {
            repoDataCleaner.clearRepoData(repoInfo, branch);
        }
    }

    private boolean wasBranchForcePushed(@NotNull String branch) throws IOException {
        final Commit lastIndexed = commitManager.getLastIndexed(repoInfo.getRepoName(), branch);
        if (lastIndexed == null) {
            return false;
        }

        try {
            repoInfo.getApiAdapter().getCommit(lastIndexed.getCommitSha1());
        } catch (FileNotFoundException ignored) {
            return true;
        }

        return false;
    }
}
