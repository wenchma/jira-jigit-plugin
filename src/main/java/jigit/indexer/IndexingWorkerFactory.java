package jigit.indexer;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public interface IndexingWorkerFactory {
    @NotNull
    Collection<IndexingWorker> build(@NotNull JigitRepo repo) throws IOException;
}
