package jigit.ao;

import jigit.entities.QueueItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface QueueItemManager {
    @NotNull
    QueueItem create(@NotNull String commitSha1, @NotNull String repoName, @NotNull String branchName);

    @NotNull
    Collection<QueueItem> getQueueItems(@NotNull String repoName, @NotNull String branchName);

    void remove(@NotNull String repoName, @NotNull String branchName, @NotNull String commitSha1);

    void remove(@NotNull String repoName, @NotNull String branchName);
}
