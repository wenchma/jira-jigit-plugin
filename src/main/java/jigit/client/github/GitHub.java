package jigit.client.github;

import api.client.http.ApiHttpRequestor;
import api.client.http.ErrorListener;
import api.client.http.HttpMethod;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class GitHub {
    @NotNull
    private static final List<String> TIME_FORMATS = Arrays.asList("yyyy/MM/dd HH:mm:ss ZZZZ", "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
    @NotNull
    private static final String githubApiUrl = "https://api.github.com";
    @NotNull
    private final String oauthToken;
    private final int requestTimeout;
    @NotNull
    private final ErrorListener errorListener;

    @NotNull
    public static GitHub connect(@NotNull String oauthToken, int requestTimeout, @NotNull ErrorListener errorListener) {
        return new GitHub(oauthToken, requestTimeout, errorListener);
    }

    @NotNull
    public static GitHub connect(@NotNull String oauthToken, @NotNull ErrorListener errorListener) {
        return connect(oauthToken, 0, ErrorListener.EMPTY);
    }

    @NotNull
    public static GitHub connect(@NotNull String oauthToken) {
        return connect(oauthToken, ErrorListener.EMPTY);
    }

    private GitHub(@NotNull String oauthToken, int requestTimeout, @NotNull ErrorListener errorListener) {
        this.oauthToken = oauthToken;
        this.requestTimeout = requestTimeout;
        this.errorListener = errorListener;
    }

    @NotNull
    public GitHubRepositoryAPI getRepositoryAPI(@NotNull String repository) {
        return new GitHubRepositoryAPI(repository, this);
    }

    @NotNull
    public ApiHttpRequestor get(@NotNull String endpointUrl) throws IOException {
        return new ApiHttpRequestor(getAPIUrl(endpointUrl), requestTimeout, errorListener, "Authorization: token " + oauthToken);
    }

    @NotNull
    public ApiHttpRequestor post(@NotNull String endpointUrl) throws IOException {
        return new ApiHttpRequestor(getAPIUrl(endpointUrl), requestTimeout, errorListener).withMethod(HttpMethod.POST);
    }

    @NotNull
    private static URL getAPIUrl(@NotNull String tailAPIUrl) throws IOException {
        final String maybeSlash = tailAPIUrl.startsWith("/") ? "" : "/";
        return new URL(githubApiUrl + maybeSlash + tailAPIUrl);
    }

    @NotNull
    public static Date parseDate(@NotNull String representation) {
        for (String timeFormat : TIME_FORMATS) {
            try {
                final SimpleDateFormat e = new SimpleDateFormat(timeFormat);
                e.setTimeZone(TimeZone.getTimeZone("GMT"));
                return e.parse(representation);
            } catch (ParseException ignore) {
            }
        }
        throw new IllegalStateException("Unable to parse the timestamp: " + representation);
    }
}
