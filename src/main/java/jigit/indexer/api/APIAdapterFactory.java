package jigit.indexer.api;

import jigit.indexer.api.github.GithubAPIAdapter;
import jigit.indexer.api.github.RateLimitHandlerJigitImpl;
import jigit.indexer.api.gitlab.GitlabAPIAdapter;
import jigit.indexer.api.gitlab.GitlabAPIExceptionHandler;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.apache.log4j.Logger;
import org.gitlab.api.GitlabAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;
import org.kohsuke.github.extras.ImpatientHttpConnector;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public final class APIAdapterFactory {
    @NotNull
    private static final Pattern GITHUB_URL_REGEXP = Pattern.compile("^.+github.com.*$", Pattern.CASE_INSENSITIVE);
    @NotNull
    private static final Logger LOG = Logger.getLogger(APIAdapterFactory.class);
    @NotNull
    private final JigitSettingsManager settingsManager;

    public APIAdapterFactory(@NotNull JigitSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @NotNull
    private GitlabAPIAdapter getGitlabAPIAdapter(@NotNull JigitRepo repo) {
        final GitlabAPI gitlabAPI = GitlabAPI.connect(repo.getServerUrl(), repo.getToken());
        gitlabAPI.setRequestTimeout(repo.getRequestTimeout());

        final GitlabAPIExceptionHandler apiExceptionHandler = new GitlabAPIExceptionHandler(settingsManager, repo);
        return new GitlabAPIAdapter(gitlabAPI, repo.getRepositoryId(), apiExceptionHandler);
    }

    @Nullable
    public APIAdapter getAPIAdapter(@NotNull JigitRepo repo) {
        final String serverUrl = repo.getServerUrl();
        if (GITHUB_URL_REGEXP.matcher(serverUrl).matches()) {
            return getGithubAPIAdapter(repo);
        }

        return getGitlabAPIAdapter(repo);
    }

    @Nullable
    private GithubAPIAdapter getGithubAPIAdapter(@NotNull JigitRepo repo) {
        try {
            final int requestTimeout = repo.getRequestTimeout();
            final ImpatientHttpConnector connector =
                    new ImpatientHttpConnector(HttpConnector.DEFAULT, requestTimeout, requestTimeout);
            final RateLimitHandler rateLimitHandler = new RateLimitHandlerJigitImpl(settingsManager, repo);
            final GitHub gitHub = GitHubBuilder.fromProperties(new Properties()).
                    withOAuthToken(repo.getToken()).
                    withConnector(connector).
                    withRateLimitHandler(rateLimitHandler).build();

            final GHRepository repository = gitHub.getRepository(repo.getRepositoryId());

            return new GithubAPIAdapter(repository);
        } catch (LimitExceededException e) {
            LOG.info("API rate limit reached. Current repo: " + repo.getRepoName());
            return null;
        } catch (IOException e) {
            LOG.error("Couldn't connect to GitHub repository " + repo.getRepoName(), e);
            return null;
        }
    }
}