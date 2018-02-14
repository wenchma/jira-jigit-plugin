package jigit.client.gitlab.dto;

import jigit.indexer.api.RepoAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GitLabProject implements RepoAdapter {
    @Nullable
    private final String path_with_namespace;
    @Nullable
    private final String default_branch;

    public GitLabProject(@Nullable String pathWithNamespace, @Nullable String defaultBranch) {
        this.path_with_namespace = pathWithNamespace;
        this.default_branch = defaultBranch;
    }

    @Nullable
    public String fullName() {
        return path_with_namespace;
    }

    @Nullable public String defaultBranch() {
        return default_branch;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GitLabProject)) return false;

        final GitLabProject gitLabProject = (GitLabProject) obj;

        return (path_with_namespace != null ? path_with_namespace.equals(gitLabProject.path_with_namespace) : gitLabProject.path_with_namespace == null) && (default_branch != null ? default_branch.equals(gitLabProject.default_branch) : gitLabProject.default_branch == null);
    }

    @Override
    public int hashCode() {
        int result = path_with_namespace != null ? path_with_namespace.hashCode() : 0;
        result = 31 * result + (default_branch != null ? default_branch.hashCode() : 0);
        return result;
    }

    @Override
    @NotNull
    public String toString() {
        return "GitLabProject{" +
                "path_with_namespace='" + path_with_namespace + '\'' +
                ", default_branch='" + default_branch + '\'' +
                '}';
    }
}
