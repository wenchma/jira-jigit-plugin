package jigit.client.gitlab.dto;

import jigit.indexer.api.RepoAdapter;
import org.jetbrains.annotations.NotNull;

public final class GitLabProject implements RepoAdapter {
    @NotNull
    private final String path_with_namespace;
    @NotNull
    private final String default_branch;

    public GitLabProject(@NotNull String pathWithNamespace, @NotNull String defaultBranch) {
        this.path_with_namespace = pathWithNamespace;
        this.default_branch = defaultBranch;
    }

    @NotNull
    public String fullName() {
        return path_with_namespace;
    }

    @NotNull public String defaultBranch() {
        return default_branch;
    }
}
