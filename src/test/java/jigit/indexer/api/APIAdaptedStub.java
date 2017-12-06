package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

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

    @NotNull
    @Override
    public String getHeadCommitSha1(@NotNull String branch) throws IOException {
        requestsCounter.increase();
        switch (branch) {
            case MASTER:
                return commit4.getCommitSha1();
            case BRANCH1:
                return commit3.getCommitSha1();
            case BRANCH2:
                return commit5.getCommitSha1();
        }
        throw new IOException("Invalid branch name " + branch);
    }

    @Override
    public long getRequestsQuantity() {
        return requestsCounter.value();
    }

    @NotNull
    @Override
    public List<String> branches() {
        return Arrays.asList(MASTER, BRANCH1, BRANCH2);
    }
}
