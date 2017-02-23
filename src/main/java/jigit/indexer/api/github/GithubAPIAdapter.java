package jigit.indexer.api.github;

import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.RequestsCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;

import java.io.IOException;

public final class GithubAPIAdapter implements APIAdapter {
    @NotNull
    private final GHRepository repository;
    @NotNull
    private final RequestsCounter requestsCounter = new RequestsCounter();

    public GithubAPIAdapter(@NotNull GHRepository repository) {
        this.repository = repository;
    }

    @NotNull
    @Override
    public CommitAdapter getCommit(@NotNull String commitSha1) throws IOException {
        final GithubCommitAdapter commitAdapter = new GithubCommitAdapter(repository.getCommit(commitSha1));
        requestsCounter.increase();
        return commitAdapter;
    }

    @Nullable
    @Override
    public String getHeadCommitSha1(@NotNull String branch) throws IOException {
        final GHBranch ghBranch = repository.getBranches().get(branch);
        requestsCounter.increase();
        if (ghBranch == null) {
            return null;
        }
        return ghBranch.getSHA1();
    }

    @Override
    public long getRequestsQuantity() {
        return requestsCounter.value();
    }
}
