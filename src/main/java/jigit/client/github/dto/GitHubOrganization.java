package jigit.client.github.dto;

import jigit.indexer.api.RepoAdapter;
import org.jetbrains.annotations.NotNull;

public final class GitHubOrganization implements RepoAdapter {
    @NotNull
    private final String full_name;
    @NotNull
    private final String default_branch;

    public GitHubOrganization(@NotNull String fullName, @NotNull String defaultBranch) {
        this.full_name = fullName;
        this.default_branch = defaultBranch;
    }

    @NotNull public String fullName() {
        return full_name;
    }

    @NotNull public String defaultBranch() {
        return default_branch;
    }
}
