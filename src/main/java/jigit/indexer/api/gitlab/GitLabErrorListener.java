package jigit.indexer.api.gitlab;

import api.client.http.ErrorListener;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;

public enum GitLabErrorListener implements ErrorListener {
    INSTANCE {
        @Override
        public void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) throws IOException {
            if (e instanceof FileNotFoundException) {
                throw e;
            }
        }
    }
}
