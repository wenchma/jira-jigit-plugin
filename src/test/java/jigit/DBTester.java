package jigit;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import jigit.ao.CommitManager;
import jigit.ao.CommitManagerImpl;
import jigit.client.github.dto.GitHubAuthor;
import jigit.client.github.dto.GitHubCommit;
import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
import jigit.entities.QueueItem;
import jigit.indexer.api.CommitFileAdapter;
import jigit.indexer.api.github.GithubCommitAdapter;
import jigit.indexer.api.github.GithubCommitFileAdapter;
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

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestDatabaseUpdater.class)
@Jdbc(DynamicJdbcConfiguration.class)
@NameConverters
public abstract class DBTester {
    @NotNull
    protected static final String REPO_NAME = "my_repo";
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
    public void setUp() {
        activeObjects = new TestActiveObjects(entityManager);
        final ChangeHistoryManager changeHistoryManager = mock(ChangeHistoryManager.class);
        when(changeHistoryManager.getPreviousIssueKeys(anyLong())).thenReturn(Collections.<String>emptyList());
        commitManager = new CommitManagerImpl(activeObjects, changeHistoryManager);
    }

    @After
    public void tearDown() {
        activeObjects.deleteWithSQL(CommitIssue.class, "1 = 1");
        activeObjects.deleteWithSQL(CommitDiff.class, "1 = 1");
        activeObjects.deleteWithSQL(Commit.class, "1 = 1");
        activeObjects.deleteWithSQL(QueueItem.class, "1 = 1");
    }

    protected void createCommitStuff(@NotNull String branch, @NotNull List<String> issueKeys) throws ParseException {
        final String sha1 = UUID.randomUUID().toString();
        createCommitStuff(sha1, branch, issueKeys, "Commit message for sha=" + sha1);
    }

    protected void createCommitStuff(@NotNull String sha1, @NotNull String branch, @NotNull List<String> issueKeys, @NotNull String message) throws ParseException {
        final GitHubCommit.CommitInfo commitInfo = new GitHubCommit.CommitInfo(
                new GitHubAuthor("me", "2016/01/02 12:00:00 UTC"), message);
        final GitHubCommit gitHubCommit = new GitHubCommit(sha1, commitInfo,
                Collections.<GitHubCommit.ParentCommit>emptyList(), Collections.<GitHubCommit.File>emptyList());
        final GitHubCommit.File gitHubFile = new GitHubCommit.File("changed", "MyClass.java", "MyClass.java");
        final CommitFileAdapter githubCommitFileAdapter = new GithubCommitFileAdapter(gitHubFile);
        commitManager.persist(new GithubCommitAdapter(gitHubCommit), REPO_NAME, branch, issueKeys,
                singletonList(githubCommitFileAdapter));
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