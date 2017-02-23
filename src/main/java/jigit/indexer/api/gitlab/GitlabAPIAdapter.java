package jigit.indexer.api.gitlab;

import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.RequestsCounter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.Pagination;
import org.gitlab.api.models.GitlabCommit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public final class GitlabAPIAdapter implements APIAdapter {
    @NotNull
    private final GitlabAPI gitlabAPI;
    @NotNull
    private final String repository;
    @NotNull
    private final GitlabAPIExceptionHandler apiExceptionHandler;
    @NotNull
    private final RequestsCounter requestsCounter = new RequestsCounter();

    public GitlabAPIAdapter(@NotNull GitlabAPI gitlabAPI,
                            @NotNull String repository,
                            @NotNull GitlabAPIExceptionHandler apiExceptionHandler) {
        this.gitlabAPI = gitlabAPI;
        this.repository = repository;
        this.apiExceptionHandler = apiExceptionHandler;
    }

    @Override
    @NotNull
    public CommitAdapter getCommit(@NotNull String commitSha1) throws IOException {
        final GitlabCommit commit;
        try {
            commit = gitlabAPI.getCommit(repository, commitSha1);
            requestsCounter.increase();
        } catch (GitlabAPIException e) {
            throw apiExceptionHandler.handle(e);
        }
        return new GitlabCommitAdapter(commit, gitlabAPI, requestsCounter, repository, apiExceptionHandler);
    }

    @Override
    @Nullable
    public String getHeadCommitSha1(@NotNull String branch) throws IOException {
        final Pagination pagination = new Pagination();
        pagination.setPerPage(1);

        final List<GitlabCommit> commits;
        try {
            commits = gitlabAPI.getCommits(repository, pagination, branch);
            requestsCounter.increase();
        } catch (GitlabAPIException e) {
            throw apiExceptionHandler.handle(e);
        }
        if (commits.isEmpty()) {
            return null;
        }

        return commits.get(0).getId();
    }

    @Override
    public long getRequestsQuantity() {
        return requestsCounter.value();
    }
}

