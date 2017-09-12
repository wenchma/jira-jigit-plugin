package jigit.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import jigit.client.github.dto.GitHubAuthor;
import jigit.client.github.dto.GitHubCommit;
import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestDatabaseUpdater.class)
@Jdbc(DynamicJdbcConfiguration.class)
@NameConverters
public final class CommitManagerTest {
    @NotNull
    public static final String REPO_NAME = "my_repo";
    @SuppressWarnings({"NullableProblems", "unused"})
    @NotNull
    private EntityManager entityManager;
    @SuppressWarnings("NullableProblems")
    @NotNull
    private CommitManager commitManager;

    @Before
    public void setUp() throws Exception {
        final ActiveObjects activeObjects = new TestActiveObjects(entityManager);
        final ChangeHistoryManager changeHistoryManager = mock(ChangeHistoryManager.class);
        when(changeHistoryManager.getPreviousIssueKeys(anyLong())).thenReturn(Collections.<String>emptyList());
        commitManager = new CommitManagerImpl(activeObjects, changeHistoryManager);
    }

    @Test
    public void readCommits() throws ParseException {
        final String issueKey1 = "KEY-11";
        final String issueKey2 = "KEY-12";
        final String issueKey3 = "KEY-14";
        createCommitStuff("master", Arrays.asList(issueKey1, issueKey2));
        createCommitStuff("master", Arrays.asList(issueKey1, issueKey3));

        final List<Commit> commits = commitManager.getCommits(singletonList(issueKey1));
        assertEquals(2, commits.size());
        for (Commit commit : commits) {
            assertEquals(1, commit.getCommitDiffs().length);
            assertEquals(2, commit.getCommitIssues().length);
        }

        final List<Commit> commits2 = commitManager.getCommits(singletonList(issueKey2));
        assertEquals(1, commits2.size());
        final Commit commit2 = commits.get(0);
        assertEquals(1, commit2.getCommitDiffs().length);
        assertEquals(2, commit2.getCommitIssues().length);

        final List<Commit> commits3 = commitManager.getCommits(singletonList("KEY-1"));
        assertEquals(0, commits3.size());

        final List<Commit> commits4 = commitManager.getCommits(Arrays.asList(issueKey1, issueKey2));
        assertEquals(2, commits4.size());
        for (Commit commit : commits) {
            assertEquals(1, commit.getCommitDiffs().length);
            assertEquals(2, commit.getCommitIssues().length);
        }
    }

    @Test
    public void emptyCommitMessage() throws ParseException {
        final String issueKey = "KEY-1";
        createCommitStuff(UUID.randomUUID().toString(), "master", singletonList(issueKey), "");
        final List<Commit> commits = commitManager.getCommits(singletonList(issueKey));
        assertEquals(1, commits.size());
    }

    @Test
    public void clearRepo() throws ParseException, SQLException {
        final String master = "master";
        final String issueKey1 = "KEY-1";
        createCommitStuff(master, singletonList(issueKey1));
        final String branch = "feature_1";
        final String issueKey2 = "KEY-1";
        createCommitStuff(branch, singletonList(issueKey2));
        assertEquals(1, commitManager.getCommitCount(REPO_NAME, master));
        assertEquals(1, commitManager.getCommitCount(REPO_NAME, branch));

        commitManager.removeCommits(REPO_NAME, master);
        assertEquals(1, entityManager.count(Commit.class));
        assertEquals(1, entityManager.count(CommitIssue.class));
        assertEquals(1, entityManager.count(CommitDiff.class));

        commitManager.removeCommits(REPO_NAME, branch);
        assertEquals(0, entityManager.count(Commit.class));
        assertEquals(0, entityManager.count(CommitIssue.class));
        assertEquals(0, entityManager.count(CommitDiff.class));
    }

    private void createCommitStuff(@NotNull String branch, @NotNull List<String> issueKeys) throws ParseException {
        final String sha1 = UUID.randomUUID().toString();
        createCommitStuff(sha1, branch, issueKeys, "Commit message for sha=" + sha1);
    }

    private void createCommitStuff(@NotNull String sha1, @NotNull String branch, @NotNull List<String> issueKeys, @NotNull String message) throws ParseException {
        final GitHubCommit.CommitInfo commitInfo = new GitHubCommit.CommitInfo(
                new GitHubAuthor("me", "2016/01/02 12:00:00 UTC"), message);
        final GitHubCommit gitHubCommit = new GitHubCommit(sha1, commitInfo,
                Collections.<GitHubCommit.ParentCommit>emptyList(), Collections.<GitHubCommit.File>emptyList());
        final GitHubCommit.File gitHubFile = new GitHubCommit.File("changed", "MyClass.java", "MyClass.java");
        final CommitFileAdapter githubCommitFileAdapter = new GithubCommitFileAdapter(gitHubFile);
        commitManager.persist(new GithubCommitAdapter(gitHubCommit), REPO_NAME, branch, issueKeys,
                singletonList(githubCommitFileAdapter));
    }
}