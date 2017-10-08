package jigit.indexer.api;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public final class TestAPIAdapterFactory implements APIAdapterFactory {
    @NotNull
    @Override
    public APIAdapter getAPIAdapter(@NotNull JigitRepo repo) {
        return new APIAdaptedStub();
    }
}
