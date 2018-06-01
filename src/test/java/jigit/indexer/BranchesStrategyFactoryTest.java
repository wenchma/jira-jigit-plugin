package jigit.indexer;

import com.google.common.collect.Sets;
import jigit.indexer.api.APIAdaptedStub;
import jigit.indexer.branch.BranchesStrategy;
import jigit.indexer.branch.BranchesStrategyFactory;
import jigit.indexer.repository.RepoType;
import jigit.indexer.repository.ServiceType;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class BranchesStrategyFactoryTest {
    private static final @NotNull TreeSet<String> EXPECTED_BRANCHES =
            Sets.newTreeSet(Arrays.asList(APIAdaptedStub.BRANCH1, APIAdaptedStub.BRANCH2));

    @Test
    public void branchesReturnedForRepoWithIndexSelectedBranchesSettingOn() throws IOException {
        final JigitRepo repo = new JigitRepo("name", "url", ServiceType.GitHub, "token",
                RepoType.SingleRepository, "repoId", APIAdaptedStub.MASTER, true, 1,
                (int) TimeUnit.SECONDS.toMillis(1), 2, false, EXPECTED_BRANCHES);
        checkReturnedBranches(repo);
    }

    @Test
    public void branchesReturnedForRepoWithIndexAllBranchesSettingOn() throws IOException {
        final JigitRepo repo = new JigitRepo("name", "url", ServiceType.GitLab, "token",
                RepoType.SingleRepository, "repoId", APIAdaptedStub.MASTER, true, 1,
                (int) TimeUnit.SECONDS.toMillis(1), 2, true);
        checkReturnedBranches(repo);
    }

    private void checkReturnedBranches(@NotNull JigitRepo repo) throws IOException {
        final BranchesStrategy branchesStrategy =
                BranchesStrategyFactory.buildBranchesStrategy(repo, repo.getDefaultBranch(), new APIAdaptedStub());
        final SortedSet<String> branches = branchesStrategy.branches();
        assertEquals(2, branches.size());
        assertTrue(branches.containsAll(EXPECTED_BRANCHES));
    }
}
