package jigit;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import jigit.ao.CommitManager;
import jigit.ao.CommitManagerImpl;
import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
import jigit.entities.QueueItem;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DynamicJdbcConfiguration;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestDatabaseUpdater.class)
@Jdbc(DynamicJdbcConfiguration.class)
@NameConverters
public abstract class DBTester {
    @NotNull
    public static final String REPO_NAME = "my_repo";
    public static final @NotNull String GROUP_NAME = "my_group";
    @SuppressWarnings({"NullableProblems", "unused"})
    @NotNull
    private EntityManager entityManager;
    @SuppressWarnings("NullableProblems")
    @NotNull
    private CommitManager commitManager;
    @SuppressWarnings("NullableProblems")
    @NotNull
    private ActiveObjects activeObjects;

    @Before
    public void setUp() throws IOException {
        activeObjects = new TestActiveObjects(entityManager);
        final ChangeHistoryManager changeHistoryManager = mock(ChangeHistoryManager.class);
        when(changeHistoryManager.getPreviousIssueKeys(anyLong())).thenReturn(Collections.<String>emptyList());
        commitManager = new CommitManagerImpl(activeObjects, changeHistoryManager);
    }

    @After
    public void tearDown() {
        activeObjects.deleteWithSQL(CommitIssue.class, "1=1");
        activeObjects.deleteWithSQL(CommitDiff.class, "1=1");
        activeObjects.deleteWithSQL(Commit.class, "1=1");
        activeObjects.deleteWithSQL(QueueItem.class, "1=1");
    }

    @NotNull
    protected CommitManager getCommitManager() {
        return commitManager;
    }

    @NotNull
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @NotNull
    public ActiveObjects getActiveObjects() {
        return activeObjects;
    }
}