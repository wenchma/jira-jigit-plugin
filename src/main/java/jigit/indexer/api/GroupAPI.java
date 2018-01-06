package jigit.indexer.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public interface GroupAPI {
    @NotNull Collection<? extends RepoAdapter> repositories(@NotNull String groupName) throws IOException;
}
