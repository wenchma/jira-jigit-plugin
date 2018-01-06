package jigit.indexer.repository;

import jigit.Function;
import jigit.client.github.GitHub;
import jigit.client.github.GitHubOrganizationsAPI;
import jigit.client.github.GitHubRepositoryAPI;
import jigit.client.gitlab.GitLab;
import jigit.client.gitlab.GitLabGroupsAPI;
import jigit.client.gitlab.GitLabRepositoryAPI;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.github.GitHubErrorListener;
import jigit.indexer.api.github.GithubAPIAdapter;
import jigit.indexer.api.gitlab.GitLabAPIAdapter;
import jigit.indexer.api.gitlab.GitLabAPIExceptionHandler;
import jigit.indexer.api.gitlab.GitLabErrorListener;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

public final class RepoInfoFactoryImpl implements RepoInfoFactory {
    @NotNull
    private static final Pattern GITHUB_URL_REGEXP = Pattern.compile("^.+github.com.*$", Pattern.CASE_INSENSITIVE);
    @NotNull
    private final JigitSettingsManager settingsManager;

    public RepoInfoFactoryImpl(@NotNull JigitSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override @NotNull
    public Collection<RepoInfo> build(@NotNull JigitRepo repo) throws IOException {
        final String serverUrl = repo.getServerUrl();
        if (GITHUB_URL_REGEXP.matcher(serverUrl).matches()) {
            return getGithubAPIAdapter(repo);
        }

        return getGitlabAPIAdapter(repo);
    }

    @NotNull
    private Collection<RepoInfo> getGitlabAPIAdapter(@NotNull JigitRepo repo) throws IOException {
        final GitLab gitLab = GitLab.connect(repo.getServerUrl(), repo.getToken(),
                GitLabErrorListener.INSTANCE, repo.getRequestTimeout());
        final GitLabGroupsAPI groupsAPI = new GitLabGroupsAPI(gitLab);

        final GitLabAPIExceptionHandler apiExceptionHandler = new GitLabAPIExceptionHandler(settingsManager, repo);
        return repo.getRepoType().repositories(repo, groupsAPI, new Function<String, APIAdapter>() {
            @NotNull
            @Override
            public APIAdapter apply(@NotNull String arg) {
                final GitLabRepositoryAPI repositoryAPI = gitLab.getGitLabRepositoryAPI(arg);
                return new GitLabAPIAdapter(repositoryAPI, apiExceptionHandler);
            }
        });
    }

    @NotNull
    private Collection<RepoInfo> getGithubAPIAdapter(@NotNull JigitRepo repo) throws IOException {
        final int requestTimeout = repo.getRequestTimeout();
        final GitHubErrorListener errorListener = new GitHubErrorListener(settingsManager, repo);

        final GitHub gitHub = GitHub.connect(repo.getToken(), errorListener, requestTimeout);
        final GitHubOrganizationsAPI groupsAPI = new GitHubOrganizationsAPI(gitHub);

        return repo.getRepoType().repositories(repo, groupsAPI, new Function<String, APIAdapter>() {
            @NotNull
            @Override
            public APIAdapter apply(@NotNull String arg) {
                final GitHubRepositoryAPI repositoryAPI = gitHub.getRepositoryAPI(arg);
                return new GithubAPIAdapter(repositoryAPI);
            }
        });
    }
}
