package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public final class CommitAdapterStub implements CommitAdapter {
    @NotNull
    public static final String AUTHOR_PRIFIX = "Author of ";
    @NotNull
    public static final String TITLE_PREFIX = "Title for ";
    @NotNull
    private final String sha1;
    @NotNull
    private final Collection<String> parentSha1s;
    @NotNull
    private final Collection<CommitFileAdapter> diffs;

    public CommitAdapterStub(@NotNull Collection<String> parentSha1s,
                             @NotNull Collection<CommitFileAdapter> diffs) {
        this.parentSha1s = parentSha1s;
        this.diffs = diffs;
        this.sha1 = UUID.randomUUID().toString();
    }

    @NotNull
    @Override
    public String getCommitSha1() {
        return sha1;
    }

    @NotNull
    @Override
    public String getTitle() {
        return TITLE_PREFIX + sha1;
    }

    @NotNull
    @Override
    public String getAuthorName() {
        return AUTHOR_PRIFIX + sha1;
    }

    @NotNull
    @Override
    public Date getCreatedAt() {
        return new Date(System.currentTimeMillis());
    }

    @NotNull
    @Override
    public Collection<String> getParentSha1s() {
        return parentSha1s;
    }

    @NotNull
    @Override
    public Collection<CommitFileAdapter> getCommitDiffs() {
        return diffs;
    }
}
