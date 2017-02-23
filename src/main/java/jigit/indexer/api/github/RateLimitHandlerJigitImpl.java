package jigit.indexer.api.github;

import jigit.indexer.api.LimitExceededException;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.RateLimitHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

public final class RateLimitHandlerJigitImpl extends RateLimitHandler {
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final JigitRepo repo;

    public RateLimitHandlerJigitImpl(@NotNull JigitSettingsManager settingsManager, @NotNull JigitRepo repo) {
        this.settingsManager = settingsManager;
        this.repo = repo;
    }

    public void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) throws IOException {
        final String resetAt = httpURLConnection.getHeaderField("X-RateLimit-Reset");
        if (resetAt != null) {
            repo.setSleepTo(Long.parseLong(resetAt) * 1000);
            settingsManager.putJigitRepo(repo);
        }

        throw new LimitExceededException();
    }
}
