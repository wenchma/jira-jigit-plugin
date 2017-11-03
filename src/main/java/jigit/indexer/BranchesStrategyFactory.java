package jigit.indexer;

import jigit.indexer.api.APIAdapter;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public enum BranchesStrategyFactory {
    ;

    @NotNull
    public static BranchesStrategy buildBranchesStrategy(@NotNull JigitRepo repo, @NotNull APIAdapter apiAdapter) {
        return repo.isIndexAllBranches() ? new BranchesApiCall(repo, apiAdapter) : new BranchesFromConfig(repo);
    }
}
