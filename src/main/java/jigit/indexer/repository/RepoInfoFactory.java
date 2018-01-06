package jigit.indexer.repository;

import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public interface RepoInfoFactory {
    @NotNull Collection<RepoInfo> build(@NotNull JigitRepo repo) throws IOException;
}
