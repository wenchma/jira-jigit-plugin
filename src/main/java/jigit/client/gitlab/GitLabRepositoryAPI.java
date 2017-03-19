package jigit.client.gitlab;

import jigit.client.gitlab.dto.GitLabBranch;
import jigit.client.gitlab.dto.GitLabCommit;
import jigit.client.gitlab.dto.GitLabFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class GitLabRepositoryAPI {
    @NotNull
    private static final String ENCODING = "UTF-8";
    @NotNull
    private static final String PROJECTS_PATH = "/api/v3/projects";
    @NotNull
    private static final String BRANCHES_PATH = "/repository/branches";
    @NotNull
    private static final String COMMITS_PATH = "/repository/commits";
    @NotNull
    private static final String DIFF_PATH = "/diff";
    @NotNull
    private final String repositoryPath;
    @NotNull
    private final GitLab gitLab;

    public GitLabRepositoryAPI(@NotNull String repository, @NotNull GitLab gitLab) {
        this.gitLab = gitLab;
        try {
            this.repositoryPath = PROJECTS_PATH + '/' + URLEncoder.encode(repository, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    public GitLabCommit getCommit(@NotNull String sha1) throws IOException {
        return gitLab.get(repositoryPath + COMMITS_PATH + '/' + sha1)
                .withResultOf(GitLabCommit.class);
    }

    @Nullable
    public GitLabBranch getBranch(@NotNull String branchName) throws IOException {
        return gitLab.get(repositoryPath + BRANCHES_PATH + '/' + branchName)
                .withResultOf(GitLabBranch.class);
    }

    @Nullable
    public GitLabFile[] getCommitFiles(@NotNull String sha1) throws IOException {
        return gitLab.get(repositoryPath + COMMITS_PATH + '/' + sha1 + '/' + DIFF_PATH)
                .withResultOf(GitLabFile[].class);
    }
}
