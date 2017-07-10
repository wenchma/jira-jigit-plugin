package jigit.job;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import jigit.indexer.JigitIndexer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

//TODO revise deprecated
public final class JigitIndexJob implements PluginJob {
    @NotNull
    private static final Logger LOG = Logger.getLogger(JigitIndexJob.class);

    @Override
    public void execute(@NotNull Map<String, Object> params) {
        final PluginScheduler pluginScheduler = (PluginScheduler) params.get(JigitIndexJobScheduler.PLUGIN_SCHEDULER_KEY);
        if (pluginScheduler == null) {
            LOG.error("JigitIndexJob::execute - pluginScheduler is null.");
            return;
        }
        final JigitIndexer jigitIndexer = (JigitIndexer) params.get(JigitIndexJobScheduler.JIGIT_INDEXER_KEY);
        if (jigitIndexer == null) {
            LOG.error("JigitIndexJob::execute - jigitIndexer is null.");
            return;
        }

        LOG.info("Jigit index job starts.");
        JigitIndexJobScheduler.unscheduleJob(pluginScheduler);

        jigitIndexer.execute();

        LOG.info("Jigit index job ends.");

        JigitIndexJobScheduler.scheduleFromNow(pluginScheduler, params);
    }
}
