package jigit.indexer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ForcePushHandler {
    @NotNull
    ForcePushHandler DO_NOTHING = new ForcePushHandler() {
        @Override
        public void handle(@NotNull String branch) {
            //do nothing
        }
    };

    void handle(@NotNull String branch) throws IOException, InterruptedException;
}
