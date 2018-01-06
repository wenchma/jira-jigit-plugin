package jigit.client.gitlab;

import com.google.gson.reflect.TypeToken;
import jigit.client.gitlab.dto.GitLabProject;
import jigit.common.APIHelper;
import jigit.indexer.api.GroupAPI;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class GitLabGroupsAPI implements GroupAPI {
    private static final @NotNull Type LIST_OF_PROJECTS = new TypeToken<List<GitLabProject>>() {
    }.getType();
    @NotNull
    private static final String API_PATH = "/api/v3/groups";
    @NotNull
    private final GitLab gitLab;

    public GitLabGroupsAPI(@NotNull GitLab gitLab) {
        this.gitLab = gitLab;
    }

    @NotNull
    public Collection<GitLabProject> repositories(@NotNull String groupName) throws IOException {
        final List<GitLabProject> branches = gitLab.get(API_PATH +
                '/' + URLEncoder.encode(groupName, APIHelper.ENCODING) + "/projects").withResultOf(LIST_OF_PROJECTS);
        return branches == null ? Collections.<GitLabProject>emptyList() : branches;
    }
}
