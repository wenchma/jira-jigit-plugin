package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface APIAdapter {
    @NotNull
    CommitAdapter getCommit(@NotNull String commitSha1) throws IOException;

    @Nullable
    String getHeadCommitSha1(@NotNull String branch) throws IOException;

    long getRequestsQuantity();
}
