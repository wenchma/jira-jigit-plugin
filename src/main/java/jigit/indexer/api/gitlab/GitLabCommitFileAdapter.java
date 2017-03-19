package jigit.indexer.api.gitlab;

import jigit.client.gitlab.dto.GitLabFile;
import jigit.common.CommitAction;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GitLabCommitFileAdapter implements CommitFileAdapter {
    @NotNull
    private final GitLabFile gitLabFile;

    public GitLabCommitFileAdapter(@NotNull GitLabFile gitLabFile) {
        this.gitLabFile = gitLabFile;
    }

    @NotNull
    @Override
    public String getNewPath() {
        return gitLabFile.getNewPath();
    }

    @Nullable
    @Override
    public String getOldPath() {
        return gitLabFile.getOldPath();
    }

    @NotNull
    @Override
    public CommitAction getCommitAction() {
        if (gitLabFile.isNewFile()) {
            return CommitAction.ADDED;
        }

        if (gitLabFile.isRenamedFile()) {
            return CommitAction.RENAMED;
        }

        if (gitLabFile.isDeletedFile()) {
            return CommitAction.DELETED;
        }

        return CommitAction.MODIFIED;
    }
}
