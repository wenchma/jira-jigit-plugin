package jigit.job;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import jigit.indexer.JigitIndexer;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public final class JigitIndexJobScheduler implements LifecycleAware {
    @NotNull
    /*package*/ static final String PLUGIN_SCHEDULER_KEY = "pluginScheduler";
    @NotNull
    /*package*/ static final String JIGIT_INDEXER_KEY = "jigitIndexer";
    @NotNull
    private static final String JOB_NAME = "Jigit Indexer Service";
    private static final int MINUTES_TO_MILLISECONDS_MULTIPLIER = 60 * 1000;
    private static final int INTERVAL_IN_MINUTES = 2;
    @NotNull
    private final PluginScheduler pluginScheduler;
    @NotNull
    private final JigitIndexer jigitIndexer;

    public JigitIndexJobScheduler(@NotNull PluginScheduler pluginScheduler,
                                    @NotNull JigitIndexer jigitIndexer) {
        this.pluginScheduler = pluginScheduler;
        this.jigitIndexer = jigitIndexer;
    }

    public static void unscheduleJob(@NotNull PluginScheduler pluginScheduler) {
        pluginScheduler.unscheduleJob(JOB_NAME);
    }

    public static void scheduleFromNow(@NotNull PluginScheduler pluginScheduler, @NotNull Map<String, Object> params) {
        final Calendar jobStartTime = new GregorianCalendar();
        jobStartTime.add(Calendar.MINUTE, INTERVAL_IN_MINUTES);

        pluginScheduler.scheduleJob(
                JOB_NAME,
                JigitIndexJob.class,
                params,
                jobStartTime.getTime(),
                INTERVAL_IN_MINUTES * MINUTES_TO_MILLISECONDS_MULTIPLIER);

    }

    @Override
    public void onStart() {
        final Map<String, Object> params = new HashMap<>();
        params.put(PLUGIN_SCHEDULER_KEY, pluginScheduler);
        params.put(JIGIT_INDEXER_KEY, jigitIndexer);

        scheduleFromNow(pluginScheduler, params);
    }

    @Override
    public void onStop() {
        unscheduleJob(pluginScheduler);
    }
}
