package jigit.indexer.api;

import jigit.client.github.GitHub;
import jigit.client.github.GitHubRepositoryAPI;
import jigit.client.gitlab.GitLab;
import jigit.client.gitlab.GitLabRepositoryAPI;
import jigit.indexer.api.github.GitHubErrorListener;
import jigit.indexer.api.github.GithubAPIAdapter;
import jigit.indexer.api.gitlab.GitLabAPIAdapter;
import jigit.indexer.api.gitlab.GitLabAPIExceptionHandler;
import jigit.indexer.api.gitlab.GitLabErrorListener;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public final class APIAdapterFactory {
    @NotNull
    private static final Pattern GITHUB_URL_REGEXP = Pattern.compile("^.+github.com.*$", Pattern.CASE_INSENSITIVE);
    @NotNull
    private final JigitSettingsManager settingsManager;

    public APIAdapterFactory(@NotNull JigitSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @NotNull
    private GitLabAPIAdapter getGitlabAPIAdapter(@NotNull JigitRepo repo) {
        final GitLabRepositoryAPI repositoryAPI = GitLab
                .connect(repo.getServerUrl(), repo.getToken(), GitLabErrorListener.INSTANCE, repo.getRequestTimeout())
                .getGitLabRepositoryAPI(repo.getRepositoryId());

        final GitLabAPIExceptionHandler apiExceptionHandler = new GitLabAPIExceptionHandler(settingsManager, repo);
        return new GitLabAPIAdapter(repositoryAPI, apiExceptionHandler);
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
        final int requestTimeout = repo.getRequestTimeout();
        final GitHubErrorListener errorListener = new GitHubErrorListener(settingsManager, repo);
        final GitHubRepositoryAPI repositoryAPI = GitHub
                .connect(repo.getToken(), errorListener, requestTimeout)
                .getRepositoryAPI(repo.getRepositoryId());

        return new GithubAPIAdapter(repositoryAPI);
    }
}