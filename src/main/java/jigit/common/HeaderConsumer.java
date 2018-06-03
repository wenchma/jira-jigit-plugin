package jigit.common;

import org.jetbrains.annotations.NotNull;

public interface HeaderConsumer<T> {
    void accept(@NotNull T arg);
}