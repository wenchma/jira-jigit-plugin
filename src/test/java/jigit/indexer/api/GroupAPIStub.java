package jigit.indexer.api;

import jigit.DBTester;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class GroupAPIStub implements GroupAPI {
    private static final @NotNull String defaultBranch = "master";
    public static final @NotNull Collection<String> repos =
            Collections.singletonList(DBTester.GROUP_NAME + "/" + DBTester.REPO_NAME);

    @NotNull
    @Override
    public Collection<? extends RepoAdapter> repositories(@NotNull String groupName) {
        final Collection<RepoAdapter> result = new ArrayList<>();
        for (final String repo : repos) {
            result.add(new RepoAdapter() {
                @NotNull
                @Override
                public String fullName() {
                    return repo;
                }

                @NotNull
                @Override
                public String defaultBranch() {
                    return defaultBranch;
                }
            });
        }

        return result;
    }
}
