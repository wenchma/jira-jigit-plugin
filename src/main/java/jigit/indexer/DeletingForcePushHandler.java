package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
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
    private final QueueItemManager queueItemManager;
    @NotNull
    private final APIAdapter apiAdapter;

    public DeletingForcePushHandler(@NotNull CommitManager commitManager,
                                    @NotNull QueueItemManager queueItemManager,
                                    @NotNull APIAdapter apiAdapter) {
        this.commitManager = commitManager;
        this.queueItemManager = queueItemManager;
        this.apiAdapter = apiAdapter;
    }

    @Override
    public void handle(@NotNull JigitRepo repo, @NotNull String branch) throws IOException {
        if (wasBranchForcePushed(repo, branch)) {
            queueItemManager.remove(repo.getRepoName(), branch);
            commitManager.removeCommits(repo.getRepoName(), branch);
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
