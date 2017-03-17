package jigit.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import jigit.common.CommitDateHelper;
import jigit.entities.Commit;
import jigit.entities.CommitDiff;
import jigit.entities.CommitIssue;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.*;

public final class CommitManagerImpl implements CommitManager {
    @NotNull
    private final ActiveObjects ao;
    @NotNull
    private final ChangeHistoryManager changeHistoryManager;

    public CommitManagerImpl(@NotNull ActiveObjects ao,
                             @NotNull ChangeHistoryManager changeHistoryManager) {
        this.ao = ao;
        this.changeHistoryManager = changeHistoryManager;
    }

    @Override
    public boolean isExists(@NotNull String repoName, @NotNull String commitSha1) {
        return ao.count(Commit.class, "REPO_NAME = ? AND COMMIT_SHA1 = ?", repoName, commitSha1) > 0;
    }

    @Nullable
    private Commit get(@NotNull String repoName, @NotNull String commitSha1) {
        final Commit[] commits = ao.find(Commit.class, "REPO_NAME = ? AND COMMIT_SHA1 = ?", repoName, commitSha1);
        if (commits.length == 0) {
            return null;
        }
        return commits[0];
    }

    @Override
    public int getCommitCount(@NotNull String repoName, @NotNull String branchName) {
        return ao.count(Commit.class, "REPO_NAME = ? AND BRANCH = ?", repoName, branchName);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    @Override
    @NotNull
    public Commit create(@NotNull String commitSha1, @NotNull String title, @NotNull String author,
                         @NotNull Date createdAt, @NotNull String repoName, @NotNull String branchName,
                         boolean firstCommit) {
        return ao.create(Commit.class, new DBParam("COMMIT_SHA1", commitSha1), new DBParam("TITLE", title),
                new DBParam("AUTHOR", author), new DBParam("CREATED_AT", createdAt),
                new DBParam("FIRST_COMMIT", firstCommit), new DBParam("REPO_NAME", repoName),
                new DBParam("BRANCH", branchName));
    }

    @Override
    @Nullable
    public Commit getLastIndexed(@NotNull String repoName, @NotNull String branch) {
        final Query lastIndexedQuery = Query.
                select().
                where("REPO_NAME = ? AND BRANCH = ?", repoName, branch).
                order("CREATED_AT DESC").
                limit(1);
        final Commit[] commits = ao.find(Commit.class, lastIndexedQuery);
        if (commits.length > 0) {
            return commits[0];
        }

        return null;
    }

    @Override
    @NotNull
    public List<Commit> getCommits(@NotNull Issue issue) {
        final Collection<String> issueKeys = new ArrayList<>();
        issueKeys.addAll(changeHistoryManager.getPreviousIssueKeys(issue.getId()));
        issueKeys.add(issue.getKey());

        final Commit[] commits = ao.find(Commit.class, Query.
                select().
                alias(Commit.class, "commit").alias(CommitIssue.class, "commit_issue").
                join(CommitIssue.class, "commit.ID = commit_issue.COMMIT_ID").
                where("commit_issue.ISSUE_KEY IN (?)", issueKeys.toArray()));

        return Arrays.asList(commits);
    }

    @Override
    @Transactional
    public void removeCommits(@NotNull String repoName, @NotNull String branch) {
        /*TODO use deleteWithSQL*/
        final Commit[] commits =
                ao.find(Commit.class, Query.select().where("REPO_NAME = ? AND BRANCH = ?", repoName, branch));

        for (Commit commit : commits) {
            ao.delete(commit.getCommitDiffs());
            ao.delete(commit.getCommitIssues());
        }

        ao.deleteWithSQL(Commit.class, "REPO_NAME = ? AND BRANCH = ?", repoName, branch);
    }

    @Override
    @NotNull
    @Transactional
    public Commit persist(@NotNull CommitAdapter commitAdapter,
                          @NotNull String repoName,
                          @NotNull String branchName,
                          @NotNull Collection<String> issueKeys,
                          @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException {
        //TODO check ao.executeInTransaction https://bitbucket.org/acourtis/rdbms-plugin-examples
        final boolean firstCommit = commitAdapter.getParentSha1s().isEmpty();
        final Date createdAt = CommitDateHelper.toUTC(commitAdapter.getCreatedAt());
        final Commit commit = create(commitAdapter.getCommitSha1(), commitAdapter.getTitle(), commitAdapter.getAuthorName(),
                createdAt, repoName, branchName, firstCommit);

        if (issueKeys.isEmpty()) {
            return commit;
        }

        final int commitId = commit.getID();
        createCommitIssues(commitId, issueKeys);
        createCommitDiffs(commitId, commitFileAdapters);

        return commit;
    }

    @Override
    @Transactional
    public void persistDependent(@NotNull CommitAdapter commitAdapter,
                                 @NotNull String repoName,
                                 @NotNull String branchName,
                                 @NotNull Collection<String> issueKeys,
                                 @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException {
        if (issueKeys.isEmpty()) {
            return;
        }
        final Commit commit = get(repoName, commitAdapter.getCommitSha1());
        if (commit == null) {
            return;
        }

        ao.delete(commit.getCommitDiffs());
        ao.delete(commit.getCommitIssues());

        final int commitId = commit.getID();
        createCommitIssues(commitId, issueKeys);
        createCommitDiffs(commitId, commitFileAdapters);
    }

    private void createCommitIssues(int commitId, @NotNull Collection<String> issueKeys) {
        for (String issueKey : issueKeys) {
            createCommitIssue(commitId, issueKey);
        }
    }

    @NotNull
    private CommitIssue createCommitIssue(int commitId, @NotNull String issueKey) {
        return ao.create(CommitIssue.class,
                new DBParam("COMMIT_ID", commitId),
                new DBParam("ISSUE_KEY", issueKey));
    }

    private void createCommitDiffs(int commitId, @NotNull Collection<CommitFileAdapter> commitFileAdapters) {
        for (CommitFileAdapter commitFileAdapter : commitFileAdapters) {
            createCommitDiff(commitId, commitFileAdapter);
        }
    }

    @NotNull
    private CommitDiff createCommitDiff(int commitId, @NotNull CommitFileAdapter commitFileAdapter) {
        final String newPath = commitFileAdapter.getNewPath();
        final String oldPath = commitFileAdapter.getOldPath();
        return ao.create(CommitDiff.class,
                new DBParam("COMMIT_ID", commitId),
                new DBParam("NEW_PATH", newPath),
                new DBParam("OLD_PATH", newPath.equals(oldPath) ? null : oldPath),
                new DBParam("ACTION_ID", commitFileAdapter.getCommitAction().getId()));
    }
}