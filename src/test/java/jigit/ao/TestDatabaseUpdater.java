package jigit.ao;

import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
import jigit.entities.QueueItem;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;
import org.jetbrains.annotations.NotNull;

public final class TestDatabaseUpdater implements DatabaseUpdater {
    @SuppressWarnings("unchecked")
    @Override
    public void update(@NotNull EntityManager entityManager) throws Exception {
        entityManager.migrate(Commit.class, CommitIssue.class, CommitDiff.class, QueueItem.class);
    }
}
