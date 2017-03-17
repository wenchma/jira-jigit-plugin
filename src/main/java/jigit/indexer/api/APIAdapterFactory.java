package jigit.indexer.api;

import jigit.client.github.GitHub;
import jigit.client.github.GitHubErrorListener;
import jigit.client.github.GitHubRepositoryAPI;
import jigit.indexer.api.github.GithubAPIAdapter;
import jigit.indexer.api.gitlab.GitlabAPIAdapter;
import jigit.indexer.api.gitlab.GitlabAPIExceptionHandler;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.gitlab.api.GitlabAPI;
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
        final int requestTimeout = repo.getRequestTimeout();
        final GitHubErrorListener errorListener = new GitHubErrorListener(settingsManager, repo);
        final GitHubRepositoryAPI repositoryAPI = GitHub
                .connect(repo.getToken(), requestTimeout, errorListener)
                .getRepositoryAPI(repo.getRepositoryId());

        return new GithubAPIAdapter(repositoryAPI);
    }
}