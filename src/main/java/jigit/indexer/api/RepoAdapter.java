package jigit.indexer.api;

import org.jetbrains.annotations.Nullable;

public interface RepoAdapter {
    @Nullable String fullName();

    @Nullable String defaultBranch();
}
