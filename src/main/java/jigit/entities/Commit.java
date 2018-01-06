package jigit.entities;

import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@SuppressWarnings("unused")
@Preload
@Table("commit")
public interface Commit extends Entity {
    @NotNull
    @Indexed
    String getCommitSha1();

    void setCommitSha1(@NotNull String commitSha1);

    @NotNull
    @net.java.ao.schema.NotNull
    @StringLength(value = StringLength.UNLIMITED)
    String getTitle();

    @net.java.ao.schema.NotNull
    @StringLength(value = StringLength.UNLIMITED)
    void setTitle(@NotNull String title);

    @NotNull
    @net.java.ao.schema.NotNull
    String getAuthor();

    @net.java.ao.schema.NotNull
    void setAuthor(@NotNull String author);

    @NotNull
    @net.java.ao.schema.NotNull
    Date getCreatedAt();

    @net.java.ao.schema.NotNull
    void setCreatedAt(@NotNull Date createdAt);

    @Indexed
    @Nullable
    String getRepoGroup();

    @Indexed
    void setRepoGroup(@Nullable String title);

    @Indexed
    @NotNull
    @net.java.ao.schema.NotNull
    String getRepoName();

    @Indexed
    @net.java.ao.schema.NotNull
    void setRepoName(@NotNull String author);

    @NotNull
    @net.java.ao.schema.NotNull
    String getBranch();

    @net.java.ao.schema.NotNull
    void setBranch(@NotNull String branch);

    @Nullable
    Boolean getFirstCommit();

    void setFirstCommit(@NotNull Boolean isFirstCommit);

    @NotNull
    @OneToMany
    CommitIssue[] getCommitIssues();

    @NotNull
    @OneToMany
    CommitDiff[] getCommitDiffs();
}
