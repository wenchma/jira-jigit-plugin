package jigit.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("unused")
public final class JigitRepo {
    @NotNull
    private final String repoName;
    @NotNull
    private final String serverUrl;
    @NotNull
    private final String token;
    @NotNull
    private final String repositoryId;
    @NotNull
    private final String defaultBranch;
    @NotNull
    private final Collection<String> branches;
    private final boolean enabled;
    private final int requestTimeout;
    private final int sleepTimeout;
    private final int sleepRequests;
    private long sleepTo;

    public JigitRepo(@NotNull String repoName,
                       @NotNull String serverUrl,
                       @NotNull String token,
                       @NotNull String repositoryId,
                       @NotNull String defaultBranch,
                       boolean enabled,
                       int requestTimeout,
                       int sleepTimeout,
                       int sleepRequests) {
        this.repoName = repoName;
        this.serverUrl = serverUrl;
        this.token = token;
        this.repositoryId = repositoryId;
        this.defaultBranch = defaultBranch;
        this.enabled = enabled;
        this.requestTimeout = requestTimeout;
        this.sleepTimeout = sleepTimeout;
        this.sleepRequests = sleepRequests;
        this.sleepTo = 0L;
        this.branches = new HashSet<>();
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

    @NotNull
    public String getRepositoryId() {
        return repositoryId;
    }

    @NotNull
    public String getDefaultBranch() {
        return defaultBranch;
    }

    @NotNull
    public Collection<String> getBranches() {
        return Collections.unmodifiableCollection(branches);
    }

    public boolean addBranch(@NotNull String branch) {
        return branches.add(branch);
    }

    public boolean addBranches(@NotNull Collection<String> branches) {
        return this.branches.addAll(branches);
    }

    public boolean removeBranch(@NotNull String branch) {
        return branches.remove(branch);
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

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        @SuppressWarnings("QuestionableName")
        final JigitRepo that = (JigitRepo) other;

        return repoName.equals(that.repoName);

    }

    @Override
    public int hashCode() {
        return repoName.hashCode();
    }
}
