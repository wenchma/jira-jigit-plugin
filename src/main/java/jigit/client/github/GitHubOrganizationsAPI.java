package jigit.client.github;

import com.google.gson.reflect.TypeToken;
import jigit.client.github.dto.GitHubOrganization;
import jigit.indexer.api.GroupAPI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static jigit.common.APIHelper.ENCODING;

public final class GitHubOrganizationsAPI implements GroupAPI {
    private static final @NotNull Type LIST_OF_BRANCHES = new TypeToken<List<GitHubOrganization>>() {
    }.getType();
    @NotNull
    private static final String ORGS_PATH = "orgs";
    @NotNull
    private final GitHub gitHub;

    public GitHubOrganizationsAPI(@NotNull GitHub gitHub) {
        this.gitHub = gitHub;
    }

    @NotNull
    public Collection<GitHubOrganization> repositories(@NotNull String orgName) throws IOException {
        final List<GitHubOrganization> branches = gitHub
                .get('/' + ORGS_PATH + '/' + URLEncoder.encode(orgName, ENCODING) + "/repos")
                .withResultOf(LIST_OF_BRANCHES);
        return branches == null ? Collections.<GitHubOrganization>emptyList() : branches;
    }
}
