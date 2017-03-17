package jigit.client.github.dto;

import org.jetbrains.annotations.NotNull;

public final class GitHubBranch {
    @NotNull
    private final Commit commit;

    public GitHubBranch(@NotNull Commit commit) {
        this.commit = commit;
    }

    @NotNull
    public String getSha() {
        return commit.getSha();
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
