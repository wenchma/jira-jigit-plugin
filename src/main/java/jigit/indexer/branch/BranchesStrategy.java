package jigit.indexer.branch;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.SortedSet;

public interface BranchesStrategy {
    @NotNull
    SortedSet<String> branches() throws IOException;
}
