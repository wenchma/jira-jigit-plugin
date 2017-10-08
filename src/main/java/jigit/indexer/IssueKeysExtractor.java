package jigit.indexer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IssueKeysExtractor {
    @NotNull
    List<String> extract(@NotNull String text);
}
