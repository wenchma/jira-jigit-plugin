package jigit.client.github.dto;

import jigit.client.github.GitHub;

import java.util.Date;

public final class GitHubAuthor {
    private final String name;
    private final String email;
    private final String date;

    public GitHubAuthor(String name, String email, String date) {
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public Date getDate() {
        return GitHub.parseDate(this.date);
    }
}
