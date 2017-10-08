package jigit.indexer;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum DisabledRepos {
    instance {
        public void markDisabled(@NotNull String repo) {
            disabledRepositories.putIfAbsent(repo, repo);
        }

        public void markEnabled(@NotNull String repo) {
            disabledRepositories.remove(repo);
        }

        public boolean disabled(@NotNull String repo) {
            return disabledRepositories.get(repo) != null;
        }
    };

    @NotNull
    protected final ConcurrentMap<String, String> disabledRepositories = new ConcurrentHashMap<>();

    public abstract void markDisabled(@NotNull String repo);

    public abstract void markEnabled(@NotNull String repo);

    public abstract boolean disabled(@NotNull String repo);
}
