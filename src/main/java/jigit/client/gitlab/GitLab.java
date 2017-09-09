package jigit.client.gitlab;

import api.client.http.ErrorListener;
import jigit.client.github.ApiClient;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public final class GitLab extends ApiClient {
    @NotNull
    private final String privateToken;
    private final int requestTimeout;
    @NotNull
    private final ErrorListener errorListener;

    @NotNull
    public static GitLab connect(@NotNull String url,
                                 @NotNull String privateToken,
                                 @NotNull ErrorListener errorListener,
                                 int requestTimeout) {
        return new GitLab(url, privateToken, requestTimeout, errorListener);
    }

    private GitLab(@NotNull String url,
                   @NotNull String privateToken,
                   int requestTimeout,
                   @NotNull ErrorListener errorListener) {
        super(url);
        this.privateToken = privateToken;
        this.requestTimeout = requestTimeout;
        this.errorListener = errorListener;
    }

    @NotNull
    public GitLabRepositoryAPI getGitLabRepositoryAPI(@NotNull String repository) {
        return new GitLabRepositoryAPI(repository, this);
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
        return Collections.singletonMap("PRIVATE-TOKEN", privateToken);
    }

    @NotNull
    public static Date parseDate(@NotNull String representation) {
        try {
            return DatatypeConverter.parseDateTime(representation).getTime();
        } catch (Throwable ignore) {
            throw new IllegalStateException("Unable to parse the timestamp: " + representation);
        }
    }
}
