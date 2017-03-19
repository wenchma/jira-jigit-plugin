package jigit.client.gitlab.dto;

import org.jetbrains.annotations.NotNull;

public final class GitLabBranch {
    @NotNull
    private final GitLabBranch.Commit commit;

    public GitLabBranch(@NotNull GitLabBranch.Commit commit) {
        this.commit = commit;
    }

    @NotNull
    public String getId() {
        return commit.getId();
    }

    private static final class Commit {
        @NotNull
        private final String id;

        private Commit(@NotNull String id) {
            this.id = id;
        }

        @NotNull
        public String getId() {
            return id;
        }
    }
}
