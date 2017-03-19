package jigit.indexer;

import org.jetbrains.annotations.NotNull;

public interface PersistStrategyFactory {
    @NotNull
    PersistStrategy getStrategy(@NotNull String repoName, @NotNull String commitSha1, boolean fetchedFirstTime);
}
