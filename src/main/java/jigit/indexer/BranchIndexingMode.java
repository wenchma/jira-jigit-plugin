package jigit.indexer;

import org.jetbrains.annotations.NotNull;

public final class BranchIndexingMode {
    private final boolean stopIndexingOnException;
    @NotNull
    private final ForcePushHandler forcePushHandler;

    public BranchIndexingMode(boolean stopIndexingOnException, @NotNull ForcePushHandler forcePushHandler) {
        this.stopIndexingOnException = stopIndexingOnException;
        this.forcePushHandler = forcePushHandler;
    }

    public boolean isStopIndexingOnException() {
        return stopIndexingOnException;
    }

    @NotNull
    public ForcePushHandler getForcePushHandler() {
        return forcePushHandler;
    }
}
