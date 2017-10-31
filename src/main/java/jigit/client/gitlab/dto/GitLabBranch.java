package jigit.client.gitlab.dto;

import org.jetbrains.annotations.NotNull;

public final class GitLabBranch {
    @NotNull
    private final String name;
    @NotNull
    private final GitLabBranch.Commit commit;

    public GitLabBranch(@NotNull String name, @NotNull Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    @NotNull
    public String getId() {
        return commit.getId();
    }

    @NotNull
    public String getName() {
        return name;
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
