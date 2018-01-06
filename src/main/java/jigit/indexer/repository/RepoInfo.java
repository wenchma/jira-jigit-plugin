package jigit.indexer.repository;

import jigit.indexer.api.APIAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;

public interface RepoInfo {
    @NotNull String getRepoName();

    @NotNull String getRepoFullName();

    @Nullable String getRepoGroup();

    @NotNull String getDefaultBranch();

    int getRequestTimeout();

    int getSleepTimeout();

    int getSleepRequests();

    long getSleepTo();

    @NotNull APIAdapter getApiAdapter();

    @NotNull SortedSet<String> branches();
}
