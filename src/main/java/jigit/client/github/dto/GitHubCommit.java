package jigit.client.github.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class GitHubCommit {
    @NotNull
    private final String sha;
    @NotNull
    private final CommitInfo commit;
    @Nullable
    private final List<ParentCommit> parents;
    @NotNull
    private final List<File> files;

    public GitHubCommit(@NotNull String sha,
                        @NotNull CommitInfo commit,
                        @Nullable List<ParentCommit> parents,
                        @NotNull List<File> files) {
        this.sha = sha;
        this.commit = commit;
        this.parents = parents;
        this.files = files;
    }

    @NotNull
    public String getSha() {
        return sha;
    }

    @NotNull
    public CommitInfo getCommitInfo() {
        return commit;
    }

    @Nullable
    public List<ParentCommit> getParents() {
        return parents;
    }

    @NotNull
    public List<File> getFiles() {
        return files;
    }

    public static final class ParentCommit {
        @NotNull
        private final String sha;

        public ParentCommit(@NotNull String sha) {
            this.sha = sha;
        }

        @NotNull
        public String getSha() {
            return sha;
        }
    }

    public static final class File {
        @NotNull
        private final String status;
        @NotNull
        private final String filename;
        @Nullable
        private final String previous_filename;

        public File(@NotNull String status,
                    @NotNull String filename,
                    @Nullable String previous_filename) {
            this.status = status;
            this.filename = filename;
            this.previous_filename = previous_filename;
        }

        @NotNull
        public String getStatus() {
            return status;
        }

        @NotNull
        public String getFilename() {
            return filename;
        }

        @Nullable
        public String getPreviousFilename() {
            return previous_filename;
        }
    }

    public static final class CommitInfo {
        @NotNull
        private final GitHubAuthor author;
        @NotNull
        private final String message;

        public CommitInfo(@NotNull GitHubAuthor author, @NotNull String message) {
            this.author = author;
            this.message = message;
        }

        @NotNull
        public GitHubAuthor getAuthor() {
            return author;
        }

        @NotNull
        public String getMessage() {
            return message;
        }

    }
}
