package jigit.client.github.dto;

import jigit.client.github.GitHub;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class GitHubAuthor {
    @NotNull
    private final String name;
    @NotNull
    private final String date;

    public GitHubAuthor(@NotNull String name, @NotNull String date) {
        this.name = name;
        this.date = date;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public Date getDate() {
        return GitHub.parseDate(this.date);
    }
}
