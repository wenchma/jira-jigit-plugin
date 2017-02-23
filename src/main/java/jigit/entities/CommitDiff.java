package jigit.entities;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@Preload
@Table("commit_diff")
public interface CommitDiff extends Entity {
    @Nullable
    @StringLength(value=StringLength.UNLIMITED)
    String getOldPath();

    @StringLength(value=StringLength.UNLIMITED)
    void setOldPath(@NotNull String oldPath);

    @NotNull
    @net.java.ao.schema.NotNull
    @StringLength(value=StringLength.UNLIMITED)
    String getNewPath();

    @StringLength(value=StringLength.UNLIMITED)
    void setNewPath(@Nullable String newPath);

    @NotNull
    @net.java.ao.schema.NotNull
    Integer getActionId();

    void setActionId(@NotNull Integer actionId);

    @NotNull
    @net.java.ao.schema.NotNull
    @Indexed
    Commit getCommit();

    void setCommit(@NotNull Commit commit);
}
