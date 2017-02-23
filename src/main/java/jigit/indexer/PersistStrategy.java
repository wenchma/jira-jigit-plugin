package jigit.indexer;

import jigit.indexer.api.CommitAdapter;
import jigit.indexer.api.CommitFileAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;

public interface PersistStrategy {
    PersistStrategy DO_NOTHING = new PersistStrategy() {
        @Override
        public Collection<String> persist(@NotNull String repoName,
                                          @NotNull String branch,
                                          @NotNull CommitAdapter commitAdapter,
                                          @NotNull Collection<String> issueKeys,
                                          @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException {
            return Collections.emptyList();
        }
    };

    Collection<String> persist(@NotNull String repoName,
                               @NotNull String branch,
                               @NotNull CommitAdapter commitAdapter,
                               @NotNull Collection<String> issueKeys,
                               @NotNull Collection<CommitFileAdapter> commitFileAdapters) throws ParseException;
}
