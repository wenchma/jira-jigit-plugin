package jigit.client.github.dto;

import jigit.indexer.api.RepoAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GitHubOrganization implements RepoAdapter {
    @Nullable
    private final String full_name;
    @Nullable
    private final String default_branch;

    public GitHubOrganization(@Nullable String fullName, @Nullable String defaultBranch) {
        this.full_name = fullName;
        this.default_branch = defaultBranch;
    }

    @Nullable public String fullName() {
        return full_name;
    }

    @Nullable public String defaultBranch() {
        return default_branch;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GitHubOrganization)) return false;

        final GitHubOrganization gitHubOrganization = (GitHubOrganization) obj;

        return (full_name != null ? full_name.equals(gitHubOrganization.full_name) : gitHubOrganization.full_name == null) && (default_branch != null ? default_branch.equals(gitHubOrganization.default_branch) : gitHubOrganization.default_branch == null);
    }

    @Override
    public int hashCode() {
        int result = full_name != null ? full_name.hashCode() : 0;
        result = 31 * result + (default_branch != null ? default_branch.hashCode() : 0);
        return result;
    }

    @Override
    @NotNull
    public String toString() {
        return "GitHubOrganization{" +
                "full_name='" + full_name + '\'' +
                ", default_branch='" + default_branch + '\'' +
                '}';
    }
}
