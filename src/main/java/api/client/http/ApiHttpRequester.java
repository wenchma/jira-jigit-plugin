package api.client.http;

import api.APIException;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

public final class ApiHttpRequester {
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
    private final Map<String, String> requestProperties;
    @NotNull
    private final ErrorListener errorListener;

    public ApiHttpRequester(@NotNull URL url,
                            int requestTimeout,
                            @NotNull ErrorListener errorListener,
                            @NotNull Map<String, String> requestProperties) {
        this.url = url;
        this.requestProperties = requestProperties;
        this.requestTimeout = requestTimeout;
        this.errorListener = errorListener;
    }

    @NotNull
    public ApiHttpRequester withMethod(@NotNull HttpMethod method) {
        this.httpMethod = method;
        return this;
    }

    @Nullable
    public <T> T withResultOf(@NotNull Class<T> type) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = buildConnection();
            return parse(connection, type);
        } finally {
            IOUtils.close(connection);
        }
    }

    @NotNull
    private HttpURLConnection buildConnection() throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (requestTimeout > 0) {
            connection.setReadTimeout(requestTimeout);
        }
        connection.setRequestMethod(httpMethod.name());
        for (Map.Entry<String, String> parameter : requestProperties.entrySet()) {
            connection.setRequestProperty(parameter.getKey(), parameter.getValue());
        }
        if (HttpMethod.POST == httpMethod) {
            connection.setDoOutput(true);
        }
        return connection;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    @Nullable
    private <T> T parse(@NotNull HttpURLConnection connection, @NotNull Class<T> type) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(connection.getInputStream(), ENCODING);
            return GSON.fromJson(reader, type);
        } catch (IOException e) {
            handleError(e, connection);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return null;
    }

    private void handleError(@NotNull IOException e, @NotNull HttpURLConnection connection) throws IOException {
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