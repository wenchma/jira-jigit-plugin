package jigit.indexer;

import jigit.indexer.api.APIAdapter;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public final class BranchesApiCall implements BranchesStrategy {
    private final @NotNull JigitRepo repo;
    private final @NotNull APIAdapter apiAdapter;

    public BranchesApiCall(@NotNull JigitRepo repo, @NotNull APIAdapter apiAdapter) {
        this.repo = repo;
        this.apiAdapter = apiAdapter;
    }

    @NotNull
    @Override
    public SortedSet<String> branches() throws IOException {
        final TreeSet<String> branches = new TreeSet<>(apiAdapter.branches());
        branches.remove(repo.getDefaultBranch());
        return Collections.unmodifiableSortedSet(branches);
    }
}
