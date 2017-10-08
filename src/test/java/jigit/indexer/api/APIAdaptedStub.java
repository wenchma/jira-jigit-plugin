package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

/*

branch1         /--c3--\
master  c1--c2-----------c4---
branch2                        \--c5

*/
public final class APIAdaptedStub implements APIAdapter {
    @NotNull
    private static final CommitFileAdapters commitFiles = new CommitFileAdapters();
    @NotNull
    public static final CommitAdapter commit1 =
            new CommitAdapterStub(Collections.<String>emptyList(), singletonList(commitFiles.get()));
    @NotNull
    public static final CommitAdapter commit2 =
            new CommitAdapterStub(singletonList(commit1.getCommitSha1()), singletonList(commitFiles.get()));
    @NotNull
    public static final CommitAdapter commit3 =
            new CommitAdapterStub(singletonList(commit2.getCommitSha1()), singletonList(commitFiles.get()));
    @NotNull
    public static final CommitAdapter commit4 =
            new CommitAdapterStub(Arrays.asList(commit3.getCommitSha1(), commit2.getCommitSha1()),
                    singletonList(commitFiles.get()));
    @NotNull
    public static final CommitAdapter commit5 =
            new CommitAdapterStub(singletonList(commit4.getCommitSha1()), singletonList(commitFiles.get()));
    @NotNull
    public static final String MASTER = "master";
    @NotNull
    public static final String BRANCH1 = "branch1";
    @NotNull
    public static final String BRANCH2 = "branch2";
    @NotNull
    private final Map<String, CommitAdapter> commits = new HashMap<String, CommitAdapter>() {{
        put(commit1.getCommitSha1(), commit1);
        put(commit2.getCommitSha1(), commit2);
        put(commit3.getCommitSha1(), commit3);
        put(commit4.getCommitSha1(), commit4);
        put(commit5.getCommitSha1(), commit5);
    }};
    @NotNull
    private final RequestsCounter requestsCounter = new RequestsCounter();

    @NotNull
    @Override
    public CommitAdapter getCommit(@NotNull String commitSha1) {
        requestsCounter.increase();
        return commits.get(commitSha1);
    }

    @Nullable
    @Override
    public String getHeadCommitSha1(@NotNull String branch) {
        requestsCounter.increase();
        if (MASTER.equals(branch)) {
            return commit4.getCommitSha1();
        } else if (BRANCH1.equals(branch)) {
            return commit3.getCommitSha1();
        } else if (BRANCH2.equals(branch)) {
            return commit5.getCommitSha1();
        }
        return null;
    }

    @Override
    public long getRequestsQuantity() {
        return requestsCounter.value();
    }
}
