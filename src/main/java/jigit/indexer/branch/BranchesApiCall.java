package jigit.indexer.branch;

import jigit.indexer.api.APIAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public final class BranchesApiCall implements BranchesStrategy {
    private final @NotNull String defaultBranch;
    private final @NotNull APIAdapter apiAdapter;

    public BranchesApiCall(@NotNull String defaultBranch, @NotNull APIAdapter apiAdapter) {
        this.defaultBranch = defaultBranch;
        this.apiAdapter = apiAdapter;
    }

    @NotNull
    @Override
    public SortedSet<String> branches() throws IOException {
        final TreeSet<String> branches = new TreeSet<>(apiAdapter.branches());
        branches.remove(defaultBranch);
        return Collections.unmodifiableSortedSet(branches);
    }
}
