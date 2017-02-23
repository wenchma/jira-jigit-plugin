package jigit.indexer.api;

import jigit.common.CommitAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommitFileAdapter {
    @NotNull
    String getNewPath();

    @Nullable
    String getOldPath();

    @NotNull
    CommitAction getCommitAction();
}
