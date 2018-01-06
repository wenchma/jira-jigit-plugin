package jigit.ao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.issue.Issue;
import jigit.entities.Commit;
import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface CommitManager {
    boolean isExists(@NotNull String repoName, @NotNull String commitSha1);

    int getCommitCount(@NotNull String repoName, @NotNull String branchName);

    int getCommitCount(@NotNull String repoGroupName);

    @SuppressWarnings("MethodWithTooManyParameters")
    @NotNull
    Commit create(@NotNull String commitSha1, @NotNull String title, @NotNull String author, @NotNull Date createdAt,
                  @Nullable String repoGroupName, @NotNull String repoName, @NotNull String branchName, boolean firstCommit);

    @Nullable
    Commit getLastIndexed(@NotNull String repoName, @NotNull String branch);

    @NotNull
    List<Commit> getCommits(@NotNull Issue issue);

    @NotNull
    List<Commit> getCommits(@NotNull Collection<String> issueKeys);

    @Transactional
    void removeCommits(@NotNull String repoName, @NotNull String branch);

    @NotNull
    @Transactional
    Commit persist(@NotNull CommitAdapter commitAdapter, @Nullable String repoGroupName,
                   @NotNull String repoName, @NotNull String branchName,
                   @NotNull Collection<String> issueKeys, @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException;

    @Transactional
    void persistDependent(@NotNull CommitAdapter commitAdapter,
                          @NotNull String repoName,
                          @NotNull String branchName,
                          @NotNull Collection<String> issueKeys,
                          @NotNull Collection<CommitFileAdapter> commitFileAdapters);
}
