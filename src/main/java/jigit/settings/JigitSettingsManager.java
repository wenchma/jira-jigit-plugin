package jigit.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface JigitSettingsManager {
    void putJigitRepo(@NotNull JigitRepo repo);

    void removeJigitRepo(@NotNull String repoName);

    @Nullable
    JigitRepo getJigitRepo(@NotNull String repoName);

    @NotNull
    Map<String, JigitRepo> getJigitRepos();
}
