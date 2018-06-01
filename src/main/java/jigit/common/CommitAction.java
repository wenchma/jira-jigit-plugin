package jigit.common;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public enum CommitAction {
    ADDED(1, "Added"),
    MODIFIED(2, "Modified"),
    RENAMED(3, "Renamed"),
    DELETED(4, "Deleted");

    public static final @NotNull Collection<CommitAction> values = Arrays.asList(CommitAction.values());
    private final int id;
    @NotNull
    private final String name;

    CommitAction(int id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    @NotNull
    public String getName() {
        return name;
    }
}
