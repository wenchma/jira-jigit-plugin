package jigit.indexer.repository;

import jigit.indexer.api.APIAdapter;
import jigit.indexer.branch.BranchesStrategyFactory;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.SortedSet;

public final class RepoInfoDirectProxy implements RepoInfo {
    private final @NotNull JigitRepo repo;
    private final @NotNull APIAdapter apiAdapter;
    private final @NotNull SortedSet<String> branches;

    public RepoInfoDirectProxy(@NotNull JigitRepo repo, @NotNull APIAdapter apiAdapter) throws IOException {
        this.repo = repo;
        this.apiAdapter = apiAdapter;
        branches = BranchesStrategyFactory.buildBranchesStrategy(repo, repo.getDefaultBranch(), apiAdapter).branches();
    }

    @Override
    @NotNull
    public String getRepoName() {
        return repo.getRepoName();
    }

    @Override
    @NotNull
    public String getRepoFullName() {
        return repo.getRepositoryId();
    }

    @Override
    @Nullable
    public String getRepoGroup() {
        return null;
    }

    @Override
    @NotNull
    public String getDefaultBranch() {
        return repo.getDefaultBranch();
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

    @Override
    @NotNull
    public APIAdapter getApiAdapter() {
        return apiAdapter;
    }

    @NotNull @Override public SortedSet<String> branches() {
        return branches;
    }
}
