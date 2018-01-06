package jigit.indexer.branch;

import jigit.indexer.api.APIAdapter;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public enum BranchesStrategyFactory {
    ;

    @NotNull
    public static BranchesStrategy buildBranchesStrategy(@NotNull JigitRepo repo, @NotNull String defaultBranch, @NotNull APIAdapter apiAdapter) {
        return repo.isIndexAllBranches() ? new BranchesApiCall(defaultBranch, apiAdapter) : new BranchesFromConfig(repo);
    }
}
