package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.entities.Commit;
import jigit.indexer.api.APIAdapter;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class DeletingForcePushHandler implements ForcePushHandler {
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final APIAdapter apiAdapter;
    @NotNull
    private final RepoDataCleaner repoDataCleaner;

    public DeletingForcePushHandler(@NotNull CommitManager commitManager,
                                    @NotNull APIAdapter apiAdapter,
                                    @NotNull RepoDataCleaner repoDataCleaner) {
        this.commitManager = commitManager;
        this.apiAdapter = apiAdapter;
        this.repoDataCleaner = repoDataCleaner;
    }

    @Override
    public void handle(@NotNull JigitRepo repo, @NotNull String branch) throws IOException, InterruptedException {
        if (wasBranchForcePushed(repo, branch)) {
            repoDataCleaner.clearRepoData(repo, branch);
        }
    }

    private boolean wasBranchForcePushed(@NotNull JigitRepo repo, @NotNull String branch) throws IOException {
        final Commit lastIndexed = commitManager.getLastIndexed(repo.getRepoName(), branch);
        if (lastIndexed == null) {
            return false;
        }

        try {
            apiAdapter.getCommit(lastIndexed.getCommitSha1());
        } catch (FileNotFoundException ignored) {
            return true;
        }

        return false;
    }
}
