package api.client.http;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ErrorListener {
    @NotNull
    ErrorListener EMPTY = new ErrorListener() {
        @Override
        public void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) {
            //do nothing;
        }
    };

    void onError(@NotNull IOException e, @NotNull HttpURLConnection httpURLConnection) throws IOException;
}
