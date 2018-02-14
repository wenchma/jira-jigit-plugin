package jigit.indexer.repository;

import jigit.Function;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.GroupAPI;
import jigit.indexer.api.RepoAdapter;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public enum RepoType {
    SingleRepository("jigit.settings.table.columns.repo.type.single.repository") {
        @NotNull
        @Override
        public Collection<RepoInfo> repositories(@NotNull final JigitRepo jigitRepo,
                                                 @NotNull GroupAPI groupAPI,
                                                 @NotNull Function<String, APIAdapter> apiAdapterFactory) throws IOException {
            return Collections.<RepoInfo>singletonList(
                    new RepoInfoDirectProxy(jigitRepo, apiAdapterFactory.apply(jigitRepo.getRepositoryId())));
        }
    },
    GroupOfRepositories("jigit.settings.table.columns.repo.type.group.of.repositories") {
        @NotNull
        @Override
        public Collection<RepoInfo> repositories(@NotNull JigitRepo jigitRepo,
                                                 @NotNull GroupAPI groupAPI,
                                                 @NotNull Function<String, APIAdapter> apiAdapterFactory) throws IOException {
            final Collection<RepoInfo> repos = new ArrayList<>();
            for (RepoAdapter repository : groupAPI.repositories(jigitRepo.getRepositoryId())) {
                final String repoFullName = repository.fullName();
                final String defaultBranch = repository.defaultBranch();
                if (repoFullName == null || defaultBranch == null) {
                    log.warn("Got a repository with empty name or default branch:" + repository);
                    continue;
                }
                final String repoGroupName = jigitRepo.getRepoName();
                repos.add(new RepoInfoGroupProxy(repoGroupName + ": " + repoFullName, repoFullName,
                        repoGroupName, defaultBranch, jigitRepo, apiAdapterFactory.apply(repoFullName)));
            }

            return repos;
        }
    };
    private static final @NotNull Logger log = LoggerFactory.getLogger(RepoType.class);

    @NotNull
    private final String displayName;

    RepoType(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    public abstract Collection<RepoInfo> repositories(@NotNull final JigitRepo jigitRepo,
                                                      @NotNull GroupAPI groupAPI,
                                                      @NotNull Function<String, APIAdapter> apiAdapterFactory) throws IOException;
}
