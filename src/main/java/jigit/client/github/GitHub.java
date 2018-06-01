package jigit.client.github;

import api.client.http.ErrorListener;
import jigit.indexer.repository.ServiceType;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class GitHub extends ApiClient {
    @NotNull
    private static final List<String> TIME_FORMATS = Arrays.asList("yyyy/MM/dd HH:mm:ss ZZZZ", "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
    @NotNull
    private static final String SITE_API_URL = "https://api.github.com";
    @NotNull
    private static final String ENTERPRISE_API_SUFFIX = "/api/v3";
    @NotNull
    private final String oauthToken;
    private final int requestTimeout;
    @NotNull
    private final ErrorListener errorListener;

    @NotNull
    public static GitHub connect(@NotNull String serverUrl, @NotNull String oauthToken,
                                 @NotNull ErrorListener errorListener, int requestTimeout) {
        return new GitHub(serverUrl, oauthToken, requestTimeout, errorListener);
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

    private GitHub(@NotNull String serverUrl, @NotNull String oauthToken, int requestTimeout,
                   @NotNull ErrorListener errorListener) {
        super(getApiUrl(serverUrl));
        this.oauthToken = oauthToken;
        this.requestTimeout = requestTimeout;
        this.errorListener = errorListener;
    }

    @NotNull
    public GitHubRepositoryAPI getRepositoryAPI(@NotNull String repository) {
        return new GitHubRepositoryAPI(repository, this);
    }

    @NotNull
    @Override
    protected ErrorListener getErrorListener() {
        return errorListener;
    }

    @Override
    protected int getRequestTimeout() {
        return requestTimeout;
    }

    @Override
    @NotNull
    protected Map<String, String> getRequestParameters() {
        return Collections.singletonMap("Authorization", "token " + oauthToken);
    }

    @NotNull
    private static String getApiUrl(@NotNull String serverUrl) {
        return ServiceType.isGitHubSite(serverUrl) ?
                SITE_API_URL : (serverUrl.trim().replaceAll("/+$", "") + ENTERPRISE_API_SUFFIX);
    }
}
