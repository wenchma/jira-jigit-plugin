package jigit.indexer.api.gitlab;

import api.APIException;
import jigit.indexer.api.LimitExceededException;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class GitLabAPIExceptionHandler {
    private static final int CODE_TOO_MANY_REQUESTS = 429;
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final JigitRepo repo;

    public GitLabAPIExceptionHandler(@NotNull JigitSettingsManager settingsManager, @NotNull JigitRepo repo) {
        this.settingsManager = settingsManager;
        this.repo = repo;
    }

    @NotNull
    public IOException handle(@NotNull APIException e) {
        if (e.getResponseCode() == CODE_TOO_MANY_REQUESTS) {
            repo.setSleepTo(System.currentTimeMillis() + repo.getSleepTimeout());
            settingsManager.putJigitRepo(repo);
            return new LimitExceededException();
        }

        return e;
    }
}
