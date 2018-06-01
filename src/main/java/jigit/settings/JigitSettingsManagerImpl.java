package jigit.settings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JigitSettingsManagerImpl implements JigitSettingsManager {
    @NotNull
    private static final String JIGIT_PLUGIN_KEY = "com.apanasevich.jira-jigit-plugin";
    @NotNull
    private static final String JIGIT_REPO_KEY = "JigitRepo";
    @NotNull
    private final PluginSettings pluginSettings;
    @NotNull
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new JigitRepoTypeAdapterFactory())
            .registerTypeAdapterFactory(new JigitServerTypeAdapterFactory())
            .create();

    public JigitSettingsManagerImpl(@NotNull PluginSettingsFactory pluginSettingsFactory) {
        pluginSettings = pluginSettingsFactory.createSettingsForKey(JIGIT_PLUGIN_KEY);
    }

    @Override
    public void putJigitRepo(@NotNull JigitRepo repo) {
        final Map<String, JigitRepo> repos = getRepos();

        repos.put(repo.getRepoName(), repo);

        pluginSettings.put(JIGIT_REPO_KEY, gson.toJson(repos));
    }


    @Override
    public void removeJigitRepo(@NotNull String repoName) {
        final Map<String, JigitRepo> repos = getRepos();

        repos.remove(repoName);

        pluginSettings.put(JIGIT_REPO_KEY, gson.toJson(repos));
    }

    @Nullable
    @Override
    public JigitRepo getJigitRepo(@NotNull String repoName) {
        final Map<String, JigitRepo> repos = getRepos();

        return repos.get(repoName);
    }

    @NotNull
    @Override
    public Map<String, JigitRepo> getJigitRepos() {
        return Collections.unmodifiableMap(getRepos());
    }

    @NotNull
    private Map<String, JigitRepo> getRepos() {
        @SuppressWarnings("EmptyClass") final Type settingsType = new TypeToken<Map<String, JigitRepo>>() {
        }.getType();

        final Map<String, JigitRepo> repos = gson.fromJson((String) pluginSettings.get(JIGIT_REPO_KEY), settingsType);

        return repos == null ? new HashMap<String, JigitRepo>() : repos;
    }
}
