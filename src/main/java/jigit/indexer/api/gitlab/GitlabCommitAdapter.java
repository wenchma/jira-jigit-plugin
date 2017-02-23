package jigit.indexer.api.gitlab;

import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import jigit.indexer.api.RequestsCounter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabCommitDiff;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public final class GitlabCommitAdapter implements CommitAdapter {
    @NotNull
    private final GitlabCommit gitlabCommit;
    @NotNull
    private final GitlabAPI gitlabAPI;
    @NotNull
    private final RequestsCounter requestsCounter;
    @NotNull
    private final String repository;
    @NotNull
    private final GitlabAPIExceptionHandler apiExceptionHandler;

    public GitlabCommitAdapter(@NotNull GitlabCommit gitlabCommit,
                               @NotNull GitlabAPI gitlabAPI,
                               @NotNull RequestsCounter requestsCounter, @NotNull String repository,
                               @NotNull GitlabAPIExceptionHandler apiExceptionHandler) {
        this.gitlabCommit = gitlabCommit;
        this.gitlabAPI = gitlabAPI;
        this.requestsCounter = requestsCounter;
        this.repository = repository;
        this.apiExceptionHandler = apiExceptionHandler;
    }

    @NotNull
    @Override
    public String getCommitSha1() {
        return gitlabCommit.getId();
    }

    @NotNull
    @Override
    public String getTitle() {
        return gitlabCommit.getTitle();
    }

    @NotNull
    @Override
    public String getAuthorName() {
        return gitlabCommit.getAuthorName();
    }

    @NotNull
    @Override
    public Date getCreatedAt() {
        return gitlabCommit.getCreatedAt();
    }

    @NotNull
    @Override
    public Collection<String> getParentSha1s() {
        final List<String> parentSha1s = gitlabCommit.getParentIds();
        return (parentSha1s == null) ? Collections.<String>emptyList() : parentSha1s;
    }

    @NotNull
    @Override
    public Collection<CommitFileAdapter> getCommitDiffs() throws IOException {
        final String commitSha1 = getCommitSha1();
        final List<GitlabCommitDiff> commitDiffs;
        try {
            commitDiffs = gitlabAPI.getCommitDiffs(repository, commitSha1);
            requestsCounter.increase();
        } catch (GitlabAPIException e) {
            throw apiExceptionHandler.handle(e);
        }
        final Collection<CommitFileAdapter> commitFileAdapters = new ArrayList<>();
        for (GitlabCommitDiff commitDiff : commitDiffs) {
            commitFileAdapters.add(new GitlabCommitFileAdapter(commitDiff));
        }

        return commitFileAdapters;
    }
}
