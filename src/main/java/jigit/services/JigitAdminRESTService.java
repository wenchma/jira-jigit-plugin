package jigit.services;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import jigit.indexer.api.APIAdapterFactory;
import jigit.settings.JigitRepo;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Path("/")
public final class JigitAdminRESTService {
    @NotNull
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    @NotNull
    private final JiraAuthenticationContext authCtx;
    @NotNull
    private final PermissionManager permissionManager;
    @NotNull
    private final I18nHelper i18n;
    @NotNull
    private final JigitSettingsManager settingsManager;
    @SuppressWarnings({"NotInitAndNotUsedInspection", "NullableProblems"})
    @NotNull
    @Context
    private HttpServletRequest request;
    private final @NotNull
    APIAdapterFactory apiAdapterFactory;

    public JigitAdminRESTService(@NotNull JiraAuthenticationContext authCtx,
                                 @NotNull PermissionManager permissionManager,
                                 @NotNull JigitSettingsManager settingsManager,
                                 @NotNull APIAdapterFactory apiAdapterFactory) {
        this.authCtx = authCtx;
        this.permissionManager = permissionManager;
        this.i18n = authCtx.getI18nHelper();
        this.settingsManager = settingsManager;
        this.apiAdapterFactory = apiAdapterFactory;
    }

    @NotNull
    private static Response getReferrerResponse(@NotNull HttpServletRequest request) {
        final String referrer = request.getHeader("referer");
        final URI uri;
        try {
            uri = new URI(referrer);
        } catch (URISyntaxException e) {
            return Response.ok(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.seeOther(uri).build();
    }

    @NotNull
    @POST
    @Path("repo/add")
    @Produces(MediaType.TEXT_HTML)
    public Response addRepo(@NotNull @FormParam("repo_name") @DefaultValue("") String repoName,
                            @NotNull @FormParam("url") @DefaultValue("") String url,
                            @NotNull @FormParam("token") @DefaultValue("") String token,
                            @NotNull @FormParam("repository_id") @DefaultValue("") String repositoryId,
                            @NotNull @FormParam("def_branch") @DefaultValue("") String branch,
                            @NotNull @FormParam("request_timeout") @DefaultValue("10") Integer requestTimeout,
                            @NotNull @FormParam("sleep_timeout") @DefaultValue("10") Integer sleepTimeout,
                            @NotNull @FormParam("sleep_requests") @DefaultValue("100") Integer sleepRequests) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (repoName.isEmpty() || url.isEmpty() || token.isEmpty() || repositoryId.isEmpty() || branch.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }
        final JigitRepo existedJigitRepo = settingsManager.getJigitRepo(repoName);
        if (existedJigitRepo != null) {
            return Response.ok(i18n.getText("jigit.error.params.repo.exists", repoName)).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo jigitRepo = new JigitRepo(repoName.trim(), url.trim(), token, repositoryId.trim(),
                branch.trim(), true, (int) TIME_UNIT.toMillis(requestTimeout),
                (int) TIME_UNIT.toMillis(sleepTimeout), sleepRequests);

        settingsManager.putJigitRepo(jigitRepo);

        return Response.ok().build();
    }

    @NotNull
    @POST
    @Path("repo/test")
    @Produces(MediaType.TEXT_HTML)
    public Response testRepo(@NotNull @FormParam("repo_name") @DefaultValue("") String repoName,
                             @NotNull @FormParam("url") @DefaultValue("") String url,
                             @NotNull @FormParam("token") @DefaultValue("") String token,
                             @NotNull @FormParam("change_token") @DefaultValue("false") Boolean change_token,
                             @NotNull @FormParam("repository_id") @DefaultValue("") String repositoryId,
                             @NotNull @FormParam("def_branch") @DefaultValue("") String branch,
                             @NotNull @FormParam("request_timeout") @DefaultValue("10") Integer requestTimeout) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (repoName.isEmpty() || url.isEmpty() || repositoryId.isEmpty() || branch.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo existedJigitRepo = settingsManager.getJigitRepo(repoName);
        final JigitRepo jigitRepo;
        if (change_token || existedJigitRepo == null) {
            jigitRepo = new JigitRepo(repoName, url.trim(), token, repositoryId.trim(),
                    branch.trim(), true, (int) TIME_UNIT.toMillis(requestTimeout),
                    (int) TIME_UNIT.toMillis(0), 0);
        } else {
            jigitRepo = new JigitRepo(repoName, url.trim(), existedJigitRepo.getToken(), repositoryId.trim(),
                    branch.trim(), true, (int) TIME_UNIT.toMillis(requestTimeout),
                    (int) TIME_UNIT.toMillis(0), 0);
        }

        try {
            apiAdapterFactory.getAPIAdapter(jigitRepo).getHeadCommitSha1(jigitRepo.getDefaultBranch());
        } catch (IOException e) {
            return Response.ok(e.getMessage()).status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    @NotNull
    @POST
    @Path("repo/edit")
    @Produces(MediaType.TEXT_HTML)
    public Response editRepo(@NotNull @FormParam("repo_name") @DefaultValue("") String repoName,
                             @NotNull @FormParam("url") @DefaultValue("") String url,
                             @NotNull @FormParam("token") @DefaultValue("") String token,
                             @NotNull @FormParam("repository_id") @DefaultValue("") String repositoryId,
                             @NotNull @FormParam("def_branch") @DefaultValue("") String branch,
                             @Nullable @FormParam("change_token") Boolean change_token,
                             @NotNull @FormParam("request_timeout") @DefaultValue("10") Integer requestTimeout,
                             @NotNull @FormParam("sleep_timeout") @DefaultValue("10") Integer sleepTimeout,
                             @NotNull @FormParam("sleep_requests") @DefaultValue("100") Integer sleepRequest) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (change_token == null || repoName.isEmpty() || url.isEmpty() || repositoryId.isEmpty() || branch.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo jigitRepo = settingsManager.getJigitRepo(repoName);
        if (jigitRepo == null) {
            return Response.ok(i18n.getText("jigit.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        final String newToken = change_token ? token : jigitRepo.getToken();
        final JigitRepo newRepo = new JigitRepo(repoName.trim(), url.trim(), newToken, repositoryId.trim(),
                branch.trim(), jigitRepo.isEnabled(), (int) TIME_UNIT.toMillis(requestTimeout),
                (int) TIME_UNIT.toMillis(sleepTimeout), sleepRequest);
        newRepo.addBranches(jigitRepo.getBranches());

        settingsManager.putJigitRepo(newRepo);

        return Response.ok().build();
    }

    @NotNull
    @POST
    @Path("/repo/{repo:.+}/remove")
    @Produces(MediaType.TEXT_HTML)
    public Response removeRepo(@NotNull @PathParam("repo") @DefaultValue("") String repoName) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (repoName.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        settingsManager.removeJigitRepo(repoName);

        return getReferrerResponse(request);
    }

    @NotNull
    @POST
    @Path("/repo/{repo:.+}/activity")
    @Produces(MediaType.TEXT_HTML)
    public Response disableRepo(@NotNull @PathParam("repo") @DefaultValue("") String repoName,
                                @Nullable @FormParam("enabled") Boolean enabled) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (enabled == null || repoName.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }
        final JigitRepo jigitRepo = settingsManager.getJigitRepo(repoName);
        if (jigitRepo == null) {
            return Response.ok(i18n.getText("jigit.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo newRepo = new JigitRepo(jigitRepo.getRepoName(), jigitRepo.getServerUrl(),
                jigitRepo.getToken(), jigitRepo.getRepositoryId(),
                jigitRepo.getDefaultBranch(), enabled, jigitRepo.getRequestTimeout(),
                jigitRepo.getSleepTimeout(), jigitRepo.getSleepRequests());
        newRepo.addBranches(jigitRepo.getBranches());

        settingsManager.putJigitRepo(newRepo);

        return getReferrerResponse(request);
    }

    @NotNull
    @POST
    @Path("/repo/{repo:.+}/branch/add")
    @Produces(MediaType.TEXT_HTML)
    public Response addBranch(@NotNull @PathParam("repo") @DefaultValue("") String repoName,
                              @NotNull @FormParam("branch") @DefaultValue("") String branch) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (repoName.isEmpty() || branch.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo jigitRepo = settingsManager.getJigitRepo(repoName);
        if (jigitRepo == null) {
            return Response.ok(i18n.getText("jigit.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }
        jigitRepo.addBranch(branch.trim());
        settingsManager.putJigitRepo(jigitRepo);

        return Response.ok().build();
    }

    @NotNull
    @POST
    @Path("/repo/{repo:.+}/branch/{branch:.+}/remove")
    @Produces(MediaType.TEXT_HTML)
    public Response removeBranch(@NotNull @PathParam("repo") @DefaultValue("") String repoName,
                                 @NotNull @PathParam("branch") @DefaultValue("") String branch) {
        final Response response = checkUserPermissions(authCtx.getLoggedInUser(), Permissions.ADMINISTER);
        if (response != null) {
            return response;
        }
        if (repoName.isEmpty() || branch.isEmpty()) {
            return Response.ok(i18n.getText("jigit.error.params.empty")).status(Response.Status.BAD_REQUEST).build();
        }

        final JigitRepo jigitRepo = settingsManager.getJigitRepo(repoName);
        if (jigitRepo == null) {
            return Response.ok(i18n.getText("jigit.error.params.invalid")).status(Response.Status.BAD_REQUEST).build();
        }
        jigitRepo.removeBranch(branch);
        settingsManager.putJigitRepo(jigitRepo);

        return getReferrerResponse(request);
    }

    @Nullable
    private Response checkUserPermissions(@Nullable ApplicationUser user, int permission) {
        Response resp = null;
        final String errorMessage;

        if (user == null) {
            errorMessage = "User is not logged in";
            resp = Response.ok(errorMessage).status(Response.Status.UNAUTHORIZED).build();
        } else if (!permissionManager.hasPermission(permission, user)) {
            errorMessage = "Invalid permissions for " + user.getName();
            resp = Response.ok(errorMessage).status(Response.Status.FORBIDDEN).build();
        }

        return resp;
    }
}
