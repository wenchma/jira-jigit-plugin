package jigit.indexer.api.github;

import jigit.client.github.dto.GitHubCommit;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class GithubCommitAdapter implements CommitAdapter {
    @NotNull
    private final GitHubCommit ghCommit;

    public GithubCommitAdapter(@NotNull GitHubCommit ghCommit) {
        this.ghCommit = ghCommit;
    }

    @NotNull
    @Override
    public String getCommitSha1() {
        return ghCommit.getSha();
    }

    @NotNull
    @Override
    public String getTitle() {
        return ghCommit.getCommitInfo().getMessage();
    }

    @NotNull
    @Override
    public String getAuthorName() {
        return ghCommit.getCommitInfo().getAuthor().getName();
    }

    @NotNull
    @Override
    public Date getCreatedAt() {
        return ghCommit.getCommitInfo().getAuthor().getDate();
    }

    @NotNull
    @Override
    public Collection<String> getParentSha1s() {
        final List<GitHubCommit.ParentCommit> parents = ghCommit.getParents();
        if (parents == null) {
            return Collections.emptyList();
        }
        final Collection<String> parentSha1s = new ArrayList<>(parents.size());
        for (GitHubCommit.ParentCommit parent : parents) {
            parentSha1s.add(parent.getSha());
        }

        return parentSha1s;
    }

    @NotNull
    @Override
    public Collection<CommitFileAdapter> getCommitDiffs() {
        final Collection<CommitFileAdapter> commitFileAdapters = new ArrayList<>();
        for (GitHubCommit.File file : ghCommit.getFiles()) {
            commitFileAdapters.add(new GithubCommitFileAdapter(file));
        }
        return commitFileAdapters;
    }
}
