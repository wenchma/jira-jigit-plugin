package jigit.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import jigit.entities.QueueItem;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class QueueItemManagerImpl implements QueueItemManager {
    @NotNull
    private final ActiveObjects ao;

    public QueueItemManagerImpl(@NotNull ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    @NotNull
    public QueueItem create(@NotNull String commitSha1, @NotNull String repoName, @NotNull String branchName) {
        return ao.create(QueueItem.class, new DBParam("COMMIT_SHA1", commitSha1), new DBParam("REPO_NAME", repoName),
                        new DBParam("BRANCH", branchName));
    }

    @Override
    @NotNull
    public Collection<QueueItem> getQueueItems(@NotNull String repoName, @NotNull String branchName) {
        final QueueItem[] queueItems = ao.find(QueueItem.class,
                Query.select().where("REPO_NAME = ? AND BRANCH = ?", repoName, branchName));

        if (queueItems.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(queueItems);
    }

    @Override
    public void remove(@NotNull String repoName, @NotNull String branchName, @NotNull String commitSha1) {
        final QueueItem[] queueItems = ao.find(QueueItem.class,
                Query.select().where("COMMIT_SHA1 = ? AND REPO_NAME = ? AND BRANCH = ?", commitSha1, repoName, branchName));
        ao.delete(queueItems);
    }

    @Override
    public void remove(@NotNull String repoName, @NotNull String branchName) {
        final QueueItem[] queueItems = ao.find(QueueItem.class,
                Query.select().where("REPO_NAME = ? AND BRANCH = ?", repoName, branchName));
        ao.delete(queueItems);
    }
}
