package jigit.indexer;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public interface IndexingWorkerFactory {
    @NotNull
    IndexingWorker build(@NotNull JigitRepo repo);
}
