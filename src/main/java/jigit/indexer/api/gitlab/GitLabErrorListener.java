package jigit.indexer.api.gitlab;

import api.client.http.ErrorListener;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;

public final class GitLabErrorListener implements ErrorListener {
    @Override
    public void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) throws IOException {
        if (e instanceof FileNotFoundException) {
            throw e;
        }
    }
}
