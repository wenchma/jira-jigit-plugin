package api.client.http;

import api.APIException;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public final class ApiHttpRequestor {
    @NotNull
    private static final String ENCODING = "UTF-8";
    @NotNull
    private static final Gson GSON = new Gson();
    private final int requestTimeout;
    @NotNull
    private final URL url;
    @NotNull
    private HttpMethod httpMethod = HttpMethod.GET;
    @NotNull
    private final String authorization;
    @NotNull
    private final ErrorListener errorListener;

    public ApiHttpRequestor(@NotNull URL url, int requestTimeout, @NotNull ErrorListener errorListener) {
        this(url, requestTimeout, errorListener, "");
    }

    public ApiHttpRequestor(@NotNull URL url, int requestTimeout, @NotNull ErrorListener errorListener, @NotNull String authorization) {
        this.url = url;
        this.authorization = authorization;
        this.requestTimeout = requestTimeout;
        this.errorListener = errorListener;
    }

    @NotNull
    public ApiHttpRequestor withMethod(@NotNull HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    @Nullable
    public <T> T withResultOf(@NotNull Class<T> type) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod.name());
            if (!authorization.isEmpty()) {
                connection.setRequestProperty("Authorization", authorization);
            }
            if (HttpMethod.POST == httpMethod) {
                connection.setDoOutput(true);
            }

            return parse(connection, type);
        } finally {
            IOUtils.close(connection);
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    @Nullable
    private <T> T parse(@NotNull HttpURLConnection connection, @NotNull Class<T> type) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(connection.getInputStream(), ENCODING);
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            handleAPIError(e, connection);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    private void handleAPIError(@NotNull IOException e, @NotNull HttpURLConnection connection) throws IOException {
        if (e instanceof FileNotFoundException) {
            throw e;
        }
        if (e instanceof SocketTimeoutException && requestTimeout > 0) {
            throw e;
        }
        errorListener.onError(e, connection);

        InputStream errorStream = null;
        try {
            errorStream = connection.getErrorStream();
            String error = null;
            if (errorStream != null) {
                error = IOUtils.toString(errorStream, ENCODING);
            }
            throw new APIException(error, connection.getResponseCode(), e);
        } finally {
            IOUtils.closeQuietly(errorStream);
        }
    }
}