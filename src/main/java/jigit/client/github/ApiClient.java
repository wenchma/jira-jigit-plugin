package jigit.client.github;

import api.client.http.ApiHttpRequester;
import api.client.http.ErrorListener;
import api.client.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public abstract class ApiClient {
    @NotNull
    private final String apiUrl;

    protected ApiClient(@NotNull String apiUrl) {
        this.apiUrl = apiUrl;
        try {
            new URL(apiUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NotNull
    public ApiHttpRequester get(@NotNull String endpointUrl) throws IOException {
        return getApiHttpRequestor(endpointUrl);
    }

    @NotNull
    private ApiHttpRequester getApiHttpRequestor(@NotNull String endpointUrl) throws IOException {
        return new ApiHttpRequester(getAPIUrl(endpointUrl), getRequestTimeout(), getErrorListener(), getRequestParameters());
    }

    @NotNull
    protected abstract ErrorListener getErrorListener();

    @NotNull
    public ApiHttpRequester post(@NotNull String endpointUrl) throws IOException {
        return getApiHttpRequestor(endpointUrl).withMethod(HttpMethod.POST);
    }

    protected abstract int getRequestTimeout();

    @NotNull
    protected abstract Map<String, String> getRequestParameters();

    @NotNull
    private URL getAPIUrl(@NotNull String tailAPIUrl) throws IOException {
        final String maybeSlash = tailAPIUrl.startsWith("/") ? "" : "/";
        return new URL(apiUrl + maybeSlash + tailAPIUrl);
    }
}
