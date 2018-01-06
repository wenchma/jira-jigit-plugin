package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

public interface RepoAdapter {
    @NotNull String fullName();

    @NotNull String defaultBranch();
}
