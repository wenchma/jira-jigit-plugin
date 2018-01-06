package jigit.settings;

import jigit.indexer.repository.RepoType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings({"unused", "ClassWithTooManyFields"})
public final class JigitRepo {
    @NotNull
    private final String repoName;
    @NotNull
    private final String serverUrl;
    @NotNull
    private final String token;
    @NotNull
    private final RepoType repoType;
    @NotNull
    private final String repositoryId;
    @NotNull
    private final String defaultBranch;
    @NotNull
    private final SortedSet<String> branches;
    private final boolean enabled;
    private final int requestTimeout;
    private final int sleepTimeout;
    private final int sleepRequests;
    private long sleepTo;
    private final boolean indexAllBranches;

    public JigitRepo(@NotNull String repoName,
                     @NotNull String serverUrl,
                     @NotNull String token,
                     @NotNull RepoType repoType,
                     @NotNull String repositoryId,
                     @NotNull String defaultBranch,
                     boolean enabled,
                     int requestTimeout,
                     int sleepTimeout,
                     int sleepRequests,
                     boolean indexAllBranches) {
        this(repoName, serverUrl, token, repoType, repositoryId, defaultBranch, enabled, requestTimeout,
                sleepTimeout, sleepRequests, indexAllBranches, new TreeSet<String>());
    }

    public JigitRepo(@NotNull String repoName,
                     @NotNull String serverUrl,
                     @NotNull String token,
                     @NotNull RepoType repoType,
                     @NotNull String repositoryId,
                     @NotNull String defaultBranch,
                     boolean enabled,
                     int requestTimeout,
                     int sleepTimeout,
                     int sleepRequests,
                     boolean indexAllBranches,
                     @NotNull SortedSet<String> branches) {
        this.repoName = repoName;
        this.serverUrl = serverUrl;
        this.token = token;
        this.repoType = repoType;
        this.repositoryId = repositoryId;
        this.defaultBranch = defaultBranch;
        this.enabled = enabled;
        this.requestTimeout = requestTimeout;
        this.sleepTimeout = sleepTimeout;
        this.sleepRequests = sleepRequests;
        this.sleepTo = 0L;
        this.indexAllBranches = indexAllBranches;
        this.branches = new TreeSet<>(branches);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public String getRepoName() {
        return repoName;
    }

    @NotNull
    public String getServerUrl() {
        return serverUrl;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    @NotNull public RepoType getRepoType() {
        return repoType;
    }

    @NotNull
    public String getRepositoryId() {
        return repositoryId;
    }

    @NotNull
    public String getDefaultBranch() {
        return defaultBranch;
    }

    @NotNull
    public SortedSet<String> getBranches() {
        return Collections.unmodifiableSortedSet(branches);
    }

    public void addBranch(@NotNull String branch) {
        branches.add(branch);
    }

    public void addBranches(@NotNull Collection<String> branches) {
        this.branches.addAll(branches);
    }

    public void removeBranch(@NotNull String branch) {
        branches.remove(branch);
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public int getSleepTimeout() {
        return sleepTimeout;
    }

    public int getSleepRequests() {
        return sleepRequests;
    }

    public long getSleepTo() {
        return sleepTo;
    }

    public void setSleepTo(long sleepTo) {
        this.sleepTo = sleepTo;
    }

    public boolean isNeedToIndex() {
        return enabled && sleepTo < System.currentTimeMillis();
    }

    public boolean isIndexAllBranches() {
        return indexAllBranches;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        @SuppressWarnings("QuestionableName") final JigitRepo that = (JigitRepo) other;

        return repoName.equals(that.repoName);
    }

    @Override
    public int hashCode() {
        return repoName.hashCode();
    }
}
