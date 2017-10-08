package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class CommitFileAdapters {
    @NotNull
    private final List<CommitFileAdapter> commitFileAdapters = Arrays.<CommitFileAdapter>asList(
            new CommitFileAdapterStub(0),
            new CommitFileAdapterStub(1),
            new CommitFileAdapterStub(2),
            new CommitFileAdapterStub(3),
            new CommitFileAdapterStub(4)
    );
    @NotNull
    private final Random random = new Random();

    @NotNull
    public CommitFileAdapter get() {
        return commitFileAdapters.get(random.nextInt(5));
    }

}
