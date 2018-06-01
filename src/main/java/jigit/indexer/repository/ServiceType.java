package jigit.indexer.repository;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public enum ServiceType {
    GitLab, GitHub;

    public static final @NotNull Collection<ServiceType> values = Arrays.asList(ServiceType.values());
    @NotNull
    private static final Pattern GITHUB_URL_REGEXP = java.util.regex.Pattern.compile("^.+github.com.*$", Pattern.CASE_INSENSITIVE);

    public static boolean isGitHubSite(@NotNull String serverUrl) {
        return GITHUB_URL_REGEXP.matcher(serverUrl).matches();
    }
}
