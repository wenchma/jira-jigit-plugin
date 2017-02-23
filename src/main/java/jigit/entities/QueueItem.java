package jigit.entities;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Preload
@Table("queue_item")
public interface QueueItem extends Entity {
    @NotNull
    @net.java.ao.schema.NotNull
    String getCommitSha1();

    void setCommitSha1(@NotNull String commitSha1);

    @NotNull
    @net.java.ao.schema.NotNull
    String getRepoName();

    void setRepoName(@NotNull String author);

    @NotNull
    @net.java.ao.schema.NotNull
    String getBranch();

    void setBranch(@NotNull String branch);
}
