package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public interface APIAdapter {
    @NotNull
    CommitAdapter getCommit(@NotNull String commitSha1) throws IOException;

    @Nullable
    String getHeadCommitSha1(@NotNull String branch) throws IOException;

    long getRequestsQuantity();

    @NotNull
    List<String> branches() throws IOException;
}
