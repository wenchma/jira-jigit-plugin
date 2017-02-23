package jigit.indexer;

import jigit.ao.QueueItemManager;
import jigit.entities.QueueItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public final class CommitQueue {
    @NotNull
    private final QueueItemManager queueItemManager;
    @NotNull
    private final String repoName;
    @NotNull
    private final String branch;
    @NotNull
    private final Queue<String> commitSha1s = new LinkedList<>();

    public CommitQueue(@NotNull QueueItemManager queueItemManager,
                       @NotNull String repoName,
                       @NotNull String branch) {
        this.queueItemManager = queueItemManager;
        this.repoName = repoName;
        this.branch = branch;
        for (QueueItem queueItem : queueItemManager.getQueueItems(repoName, branch)) {
            commitSha1s.add(queueItem.getCommitSha1());
        }
    }

    public boolean add(@NotNull String commitSha1) {
        if (commitSha1s.contains(commitSha1)) {
            return true;
        }
        if (!commitSha1s.add(commitSha1)) {
            return false;
        }
        queueItemManager.create(commitSha1, repoName, branch);
        return true;
    }

    public boolean addAll(@Nullable Collection<String> commits) {
        if (commits == null) {
            return true;
        }

        for (String commitSha1 : commits) {
            add(commitSha1);
        }

        return true;
    }

    @Nullable
    public String peek() {
        return commitSha1s.peek();
    }

    public void remove() {
        final String commitSha1 = commitSha1s.poll();
        queueItemManager.remove(repoName, branch, commitSha1);
    }

    public boolean isEmpty() {
        return commitSha1s.isEmpty();
    }

    public int size() {
        return commitSha1s.size();
    }
}
