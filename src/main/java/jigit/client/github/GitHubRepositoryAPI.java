package jigit.client.github;

import jigit.client.github.dto.GitHubBranch;
import jigit.client.github.dto.GitHubCommit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class GitHubRepositoryAPI {
    @NotNull
    private static final String REPOS_PATH = "/repos";
    @NotNull
    private static final String COMMITS_PATH = "/commits";
    @NotNull
    private static final String BRANCHES_PATH = "/branches";
    @NotNull
    private final String repository;
    @NotNull
    private final GitHub gitHub;

    public GitHubRepositoryAPI(@NotNull String repository, @NotNull GitHub gitHub) {
        this.repository = repository.trim().replaceAll("^/+", "").replaceAll("/+$", "");
        this.gitHub = gitHub;
    }

    @Nullable
    public GitHubCommit getCommit(@NotNull String sha1) throws IOException {
        return gitHub.get(REPOS_PATH + '/' + repository + COMMITS_PATH + '/' + sha1)
                .withResultOf(GitHubCommit.class);
    }

    @Nullable
    public GitHubBranch getBranch(@NotNull String branchName) throws IOException {
        return gitHub.get(REPOS_PATH + '/' + repository + BRANCHES_PATH + '/' + branchName)
                .withResultOf(GitHubBranch.class);
    }
}
