package jigit.indexer.api.gitlab;

import jigit.common.CommitAction;
import jigit.indexer.api.CommitFileAdapter;
import org.gitlab.api.models.GitlabCommitDiff;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GitlabCommitFileAdapter implements CommitFileAdapter {
    @NotNull
    private final GitlabCommitDiff gitlabCommitDiff;

    public GitlabCommitFileAdapter(@NotNull GitlabCommitDiff gitlabCommitDiff) {
        this.gitlabCommitDiff = gitlabCommitDiff;
    }

    @NotNull
    @Override
    public String getNewPath() {
        return gitlabCommitDiff.getNewPath();
    }

    @Nullable
    @Override
    public String getOldPath() {
        return gitlabCommitDiff.getOldPath();
    }

    @NotNull
    @Override
    public CommitAction getCommitAction() {
        if (gitlabCommitDiff.getNewFile()) {
            return CommitAction.ADDED;
        }

        if (gitlabCommitDiff.getRenamedFile()) {
            return CommitAction.RENAMED;
        }

        if (gitlabCommitDiff.getDeletedFile()) {
            return CommitAction.DELETED;
        }

        return CommitAction.MODIFIED;
    }
}
