package jigit;

import org.jetbrains.annotations.NotNull;

public interface Function<F, T> {
    @NotNull T apply(@NotNull F arg);
}