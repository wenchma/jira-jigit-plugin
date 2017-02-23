package jigit.entities;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Preload
@Table("commit_issue")
public interface CommitIssue extends Entity {
    @NotNull
    @net.java.ao.schema.NotNull
    @Indexed
    String getIssueKey();

    @Indexed
    void setIssueKey(@NotNull String issueKey);

    @NotNull
    @net.java.ao.schema.NotNull
    Commit getCommit();

    void setCommit(@NotNull Commit commit);
}
