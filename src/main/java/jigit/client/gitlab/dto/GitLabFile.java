package jigit.client.gitlab.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GitLabFile {
    @NotNull
    private final String new_path;
    @Nullable
    private final String old_path;
    private final boolean new_file;
    private final boolean renamed_file;
    private final boolean deleted_file;

    public GitLabFile(@NotNull String new_path, @Nullable String old_path,
                      boolean new_file, boolean renamed_file, boolean deleted_file) {
        this.new_path = new_path;
        this.old_path = old_path;
        this.new_file = new_file;
        this.renamed_file = renamed_file;
        this.deleted_file = deleted_file;
    }

    @NotNull
    public String getNewPath() {
        return new_path;
    }

    @Nullable
    public String getOldPath() {
        return old_path;
    }

    public boolean isNewFile() {
        return new_file;
    }

    public boolean isRenamedFile() {
        return renamed_file;
    }

    public boolean isDeletedFile() {
        return deleted_file;
    }
}
