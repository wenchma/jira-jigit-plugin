package jigit.indexer.api;

import jigit.Function;
import jigit.indexer.repository.RepoInfo;
import jigit.indexer.repository.RepoInfoFactory;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public final class TestRepoInfoFactory implements RepoInfoFactory {
    @NotNull @Override public Collection<RepoInfo> build(@NotNull JigitRepo repo) throws IOException {
        return repo.getRepoType().repositories(repo, new GroupAPIStub(), new Function<String, APIAdapter>() {
            @NotNull @Override public APIAdapter apply(@NotNull String arg) {
                return new APIAdaptedStub();
            }
        });
    }
}
