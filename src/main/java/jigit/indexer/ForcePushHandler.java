package jigit.indexer;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ForcePushHandler {
    ForcePushHandler DO_NOTHING = new ForcePushHandler() {
        @Override
        public void handle(@NotNull JigitRepo repo, @NotNull String branch) throws IOException {
            //do nothing
        }
    };

    void handle(@NotNull JigitRepo repo, @NotNull String branch) throws IOException;
}
