package jigit.client.github.dto;

import org.jetbrains.annotations.NotNull;

public final class GitHubBranch {
    @NotNull
    private final String name;
    @NotNull
    private final Commit commit;

    public GitHubBranch(@NotNull String name, @NotNull Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    @NotNull
    public String getSha() {
        return commit.getSha();
    }

    @NotNull
    public String getName() {
        return name;
    }

    private static final class Commit {
        @NotNull
        private final String sha;

        private Commit(@NotNull String sha) {
            this.sha = sha;
        }

        @NotNull
        public String getSha() {
            return sha;
        }
    }
}
