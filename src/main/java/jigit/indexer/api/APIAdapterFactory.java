package jigit.indexer.api;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public interface APIAdapterFactory {
    @NotNull
    APIAdapter getAPIAdapter(@NotNull JigitRepo repo);
}
