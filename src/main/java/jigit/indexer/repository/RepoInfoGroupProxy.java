package jigit.indexer.repository;

import jigit.indexer.api.APIAdapter;
import jigit.indexer.branch.BranchesApiCall;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.SortedSet;

public final class RepoInfoGroupProxy implements RepoInfo {
    private final @NotNull String repoName;
    private final @NotNull String repoFullName;
    private final @NotNull String repoGroup;
    private final @NotNull String defaultBranch;
    private final @NotNull JigitRepo repo;
    private final @NotNull APIAdapter apiAdapter;
    private final @NotNull SortedSet<String> branches;

    public RepoInfoGroupProxy(@NotNull String repoName,
                              @NotNull String repoFullName,
                              @NotNull String repoGroup, @NotNull String defaultBranch,
                              @NotNull JigitRepo repo,
                              @NotNull APIAdapter apiAdapter) throws IOException {
        this.repoName = repoName;
        this.repoFullName = repoFullName;
        this.repoGroup = repoGroup;
        this.defaultBranch = defaultBranch;
        this.repo = repo;
        this.apiAdapter = apiAdapter;
        branches = new BranchesApiCall(defaultBranch, apiAdapter).branches();
    }

    @Override
    @NotNull
    public String getRepoName() {
        return repoName;
    }

    @Override
    @NotNull
    public String getRepoFullName() {
        return repoFullName;
    }

    @Override
    @NotNull
    public String getRepoGroup() {
        return repoGroup;
    }

    @Override
    @NotNull
    public String getDefaultBranch() {
        return defaultBranch;
    }

    @Override
    public int getRequestTimeout() {
        return repo.getRequestTimeout();
    }

    @Override
    public int getSleepTimeout() {
        return repo.getSleepTimeout();
    }

    @Override
    public int getSleepRequests() {
        return repo.getSleepRequests();
    }

    @Override
    public long getSleepTo() {
        return repo.getSleepTo();
    }

    @NotNull @Override public APIAdapter getApiAdapter() {
        return apiAdapter;
    }

    @NotNull
    @Override public SortedSet<String> branches() {
        return branches;
    }
}
