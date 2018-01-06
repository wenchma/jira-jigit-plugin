package jigit.indexer.branch;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

public final class BranchesFromConfig implements BranchesStrategy {
    private final @NotNull JigitRepo repo;

    public BranchesFromConfig(@NotNull JigitRepo repo) {
        this.repo = repo;
    }

    @NotNull
    @Override
    public SortedSet<String> branches() {
        return repo.getBranches();
    }
}
