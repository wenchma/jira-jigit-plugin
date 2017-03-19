package jigit.indexer.api.github;

import api.client.http.ErrorListener;
import jigit.indexer.api.LimitExceededException;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

public final class GitHubErrorListener implements ErrorListener {
    @NotNull
    private static final String LIMIT_THRESHOLD_VALUE = "0";
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final JigitRepo repo;

    public GitHubErrorListener(@NotNull JigitSettingsManager settingsManager, @NotNull JigitRepo repo) {
        this.settingsManager = settingsManager;
        this.repo = repo;
    }

    @Override
    public void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) throws IOException {
        if (!LIMIT_THRESHOLD_VALUE.equals(httpURLConnection.getHeaderField("X-RateLimit-Remaining"))) {
            return;
        }
        final String resetAt = httpURLConnection.getHeaderField("X-RateLimit-Reset");
        if (resetAt != null) {
            repo.setSleepTo(Long.parseLong(resetAt) * 1000);
            settingsManager.putJigitRepo(repo);
        }

        throw new LimitExceededException();
    }
}
