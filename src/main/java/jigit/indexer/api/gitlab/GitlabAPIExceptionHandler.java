package jigit.indexer.api.gitlab;

import jigit.indexer.api.LimitExceededException;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.gitlab.api.GitlabAPIException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class GitlabAPIExceptionHandler {
    private static final int CODE_TOO_MANY_REQUESTS = 429;
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final JigitRepo repo;

    public GitlabAPIExceptionHandler(@NotNull JigitSettingsManager settingsManager, @NotNull JigitRepo repo) {
        this.settingsManager = settingsManager;
        this.repo = repo;
    }

    @NotNull
    public IOException handle(@NotNull GitlabAPIException e) {
        if (e.getResponseCode() == CODE_TOO_MANY_REQUESTS) {
            repo.setSleepTo(System.currentTimeMillis() + repo.getSleepTimeout());
            settingsManager.putJigitRepo(repo);
            return new LimitExceededException();
        }

        return e;
    }
}
