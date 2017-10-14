package jigit.indexer;

import com.google.common.collect.Sets;
import jigit.DBTester;
import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.ao.QueueItemManagerImpl;
import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
import jigit.entities.QueueItem;
import jigit.indexer.api.APIAdaptedStub;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import jigit.indexer.api.TestAPIAdapterFactory;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class JigitIndexerTest extends DBTester {
    @NotNull
    @Rule
    public final Timeout timeoutRule = new Timeout((int) TimeUnit.SECONDS.toMillis(30));
    @NotNull
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @NotNull
    private final JigitRepo jigitRepo =
            new JigitRepo(REPO_NAME, "url", "token", "repoId", APIAdaptedStub.MASTER, true, 1,
                    (int) TimeUnit.SECONDS.toMillis(1), 2,
                    Sets.newTreeSet(Arrays.asList(APIAdaptedStub.BRANCH1, APIAdaptedStub.BRANCH2)));
    @NotNull
    private final JigitSettingsManager jigitSettingsManager = mock(JigitSettingsManager.class);
    @NotNull
    private final IssueKeysExtractor issueKeysExtractor = new TestIssueKeysExtractor();
    @SuppressWarnings("NullableProblems")
    @NotNull
    private JigitIndexer jigitIndexer;
    @SuppressWarnings("NullableProblems")
    @NotNull
    private RepoDataCleaner repoDataCleaner;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        final QueueItemManager queueItemManager = new QueueItemManagerImpl(getActiveObjects());
        when(jigitSettingsManager.getJigitRepos())
                .thenReturn(new HashMap<String, JigitRepo>() {{
                    put(jigitRepo.getRepoName(), jigitRepo);
                }});
        final CommitManager commitManager = getCommitManager();
        repoDataCleaner = new RepoDataCleaner(commitManager, queueItemManager);
        final IndexingWorkerFactory indexingWorkerFactory = new IndexingWorkerFactoryImpl(new TestAPIAdapterFactory(),
                queueItemManager, new PersistStrategyFactoryImpl(commitManager), commitManager,
                repoDataCleaner, issueKeysExtractor);
        jigitIndexer = new JigitIndexer(jigitSettingsManager, indexingWorkerFactory);
        DisabledRepos.instance.markEnabled(REPO_NAME);
    }

    @AfterClass
    public static void tearDownAll() {
        executorService.shutdown();
    }

    @Test
    public void repoIndexed() {
        jigitIndexer.execute();
        allCommitsAreIndexed();
    }

    @Test
    public void repoIndexingStopsAfterDisabling() throws SQLException, ExecutionException, InterruptedException {
        final Future<Boolean> futureResult = executorService.submit(new Callable<Boolean>() {
            @NotNull
            @Override
            public Boolean call() {
                try {
                    Thread.sleep(jigitRepo.getSleepTimeout());
                } catch (InterruptedException ignored) {
                    fail();
                    return Boolean.FALSE;
                }
                DisabledRepos.instance.markDisabled(REPO_NAME);
                return Boolean.TRUE;
            }
        });
        jigitIndexer.execute();
        assertTrue(futureResult.get());
        assertTrue(DisabledRepos.instance.disabled(REPO_NAME));
        final int commitsNumber = getEntityManager().count(Commit.class);
        assertTrue("Actual number is " + commitsNumber, commitsNumber < 5);
        assertTrue("Actual number is " + commitsNumber, commitsNumber >= 1);

        final List<Commit> commits4 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit4.getCommitSha1()));
        final Commit commit = assertCommits(commits4, APIAdaptedStub.MASTER);
        assertCommit(commit, APIAdaptedStub.commit4);
    }


    @Test
    public void noRepoDataAfterCleaning() throws SQLException, ExecutionException, InterruptedException {
        final Future<Boolean> futureResult = executorService.submit(new Callable<Boolean>() {
            @NotNull
            @Override
            public Boolean call() {
                try {
                    Thread.sleep(jigitRepo.getSleepTimeout());
                    final int commitsNumber = getEntityManager().count(Commit.class);
                    assertTrue("Actual number is " + commitsNumber, commitsNumber >= 1);
                    repoDataCleaner.clearRepoData(jigitRepo);
                } catch (InterruptedException | SQLException ignored) {
                    fail();
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        });
        jigitIndexer.execute();
        assertTrue(futureResult.get());
        assertFalse(DisabledRepos.instance.disabled(REPO_NAME));
        assertEquals(0, getEntityManager().count(Commit.class));
        assertEquals(0, getEntityManager().count(QueueItem.class));
    }

    private void allCommitsAreIndexed() {
        final List<Commit> commits1 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit1.getCommitSha1()));
        final Commit commit = assertCommits(commits1, APIAdaptedStub.MASTER);
        assertCommit(commit, APIAdaptedStub.commit1);

        final List<Commit> commits2 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit2.getCommitSha1()));
        final Commit commit2 = assertCommits(commits2, APIAdaptedStub.MASTER);
        assertCommit(commit2, APIAdaptedStub.commit2);

        final List<Commit> commits3 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit3.getCommitSha1()));
        final Commit commit3 = assertCommits(commits3, APIAdaptedStub.MASTER);
        assertCommit(commit3, APIAdaptedStub.commit3);

        final List<Commit> commits4 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit4.getCommitSha1()));
        final Commit commit4 = assertCommits(commits4, APIAdaptedStub.MASTER);
        assertCommit(commit4, APIAdaptedStub.commit4);

        final List<Commit> commits5 =
                getCommitManager().getCommits(singletonList(APIAdaptedStub.commit5.getCommitSha1()));
        final Commit commit5 = assertCommits(commits5, APIAdaptedStub.BRANCH2);
        assertCommit(commit5, APIAdaptedStub.commit5);
    }

    @NotNull
    private Commit assertCommits(@NotNull List<Commit> commits, @NotNull String branchName) {
        assertEquals(1, commits.size());
        final Commit commit = commits.get(0);
        assertEquals(REPO_NAME, commit.getRepoName());
        assertEquals(branchName, commit.getBranch());
        return commit;
    }

    private void assertCommit(@NotNull Commit commit, @NotNull CommitAdapter commitAdapter) {
        assertEquals(commit.getAuthor(), commitAdapter.getAuthorName());
        assertEquals(commit.getCommitSha1(), commitAdapter.getCommitSha1());
        assertEquals(commit.getTitle(), commitAdapter.getTitle());
        assertIssueKeys(commit, commitAdapter);
        try {
            assertDiffs(commit, commitAdapter);
        } catch (IOException ignored) {
            fail();
        }
    }

    private void assertDiffs(@NotNull Commit commit, @NotNull CommitAdapter commitAdapter) throws IOException {
        final CommitDiff[] storedDiffs = commit.getCommitDiffs();
        final Collection<CommitFileAdapter> commitDiffs = commitAdapter.getCommitDiffs();
        assertEquals(commitDiffs.size(), storedDiffs.length);
        assertEquals(1, commitDiffs.size());
        final CommitFileAdapter diff = commitDiffs.iterator().next();
        assertEquals(diff.getCommitAction().getId(), storedDiffs[0].getActionId().intValue());
        assertEquals(diff.getNewPath(), storedDiffs[0].getNewPath());
        assertEquals(diff.getOldPath(), storedDiffs[0].getOldPath());
    }

    private void assertIssueKeys(@NotNull Commit commit, @NotNull CommitAdapter commitAdapter) {
        final List<String> issueKeys = issueKeysExtractor.extract(commitAdapter.getTitle());
        final CommitIssue[] commitIssues = commit.getCommitIssues();
        assertEquals(issueKeys.size(), commitIssues.length);
        for (CommitIssue commitIssue : commitIssues) {
            assertTrue(issueKeys.contains(commitIssue.getIssueKey()));
        }
    }
}
