package jigit.indexer;

import jigit.indexer.api.CommitAdapterStub;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class TestIssueKeysExtractor implements IssueKeysExtractor {
    @NotNull
    @Override
    public List<String> extract(@NotNull String text) {
        return Collections.singletonList(text.substring(CommitAdapterStub.TITLE_PREFIX.length()));
    }
}
