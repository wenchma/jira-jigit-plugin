package jigit.indexer.api.github;

import com.google.common.collect.ImmutableMap;
import jigit.common.CommitAction;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHCommit;

public final class GithubCommitFileAdapter implements CommitFileAdapter {
    @NotNull
    private static final ImmutableMap<String, CommitAction> ACTIONS = ImmutableMap.of(
            "added", CommitAction.ADDED,
            "removed", CommitAction.DELETED,
            "modified", CommitAction.MODIFIED,
            "renamed", CommitAction.RENAMED);
    @NotNull
    private GHCommit.File file;

    public GithubCommitFileAdapter(@NotNull GHCommit.File file) {
        this.file = file;
    }

    @NotNull
    @Override
    public String getNewPath() {
        return file.getFileName();
    }

    @Nullable
    @Override
    public String getOldPath() {
        return file.getPreviousFilename();
    }

    @NotNull
    @Override
    public CommitAction getCommitAction() {
        final CommitAction commitAction = ACTIONS.get(file.getStatus());

        return (commitAction == null) ? CommitAction.MODIFIED : commitAction;
    }
}
