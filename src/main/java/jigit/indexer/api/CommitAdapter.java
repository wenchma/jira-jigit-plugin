package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public interface CommitAdapter {
    @NotNull
    String getCommitSha1();

    @NotNull
    String getTitle();

    @NotNull
    String getAuthorName();

    @NotNull
    Date getCreatedAt();

    @NotNull
    Collection<String> getParentSha1s();

    @NotNull
    Collection<CommitFileAdapter> getCommitDiffs() throws IOException;
}
