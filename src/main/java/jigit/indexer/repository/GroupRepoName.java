package jigit.indexer.repository;

import org.jetbrains.annotations.NotNull;

public enum GroupRepoName {
    Rule;

    private static final @NotNull String DELIMITER = ": ";

    public @NotNull String buildName(@NotNull String repoGroupName, @NotNull String repoFullName) {
        return repoGroupName + DELIMITER + repoFullName;
    }

    public @NotNull String extractRepoFullName(@NotNull String repoName) {
        final int lastIndexOf = repoName.lastIndexOf(DELIMITER);
        return (lastIndexOf < 0) ? "" : repoName.substring(lastIndexOf + DELIMITER.length());
    }
}
