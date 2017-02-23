package jigit.indexer.api.github;

import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.util.*;

public final class GithubCommitAdapter implements CommitAdapter {
    @NotNull
    private final GHCommit ghCommit;

    public GithubCommitAdapter(@NotNull GHCommit ghCommit) {
        this.ghCommit = ghCommit;
    }

    @NotNull
    @Override
    public String getCommitSha1() {
        return ghCommit.getSHA1();
    }

    @NotNull
    @Override
    public String getTitle() {
        return ghCommit.getCommitShortInfo().getMessage();
    }

    @NotNull
    @Override
    public String getAuthorName() {
        return ghCommit.getCommitShortInfo().getAuthor().getName();
    }

    @NotNull
    @Override
    public Date getCreatedAt() {
        return ghCommit.getCommitShortInfo().getAuthor().getDate();
    }

    @NotNull
    @Override
    public Collection<String> getParentSha1s() {
        final List<String> parentSha1s = ghCommit.getParentSHA1s();
        return (parentSha1s == null) ? Collections.<String>emptyList() : parentSha1s;

    }

    @NotNull
    @Override
    public Collection<CommitFileAdapter> getCommitDiffs() throws IOException {
        final Collection<CommitFileAdapter> commitFileAdapters = new ArrayList<>();
        for (GHCommit.File file : ghCommit.getFiles()) {
            commitFileAdapters.add(new GithubCommitFileAdapter(file));
        }
        return commitFileAdapters;
    }
}
