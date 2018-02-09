package jigit.job;

import com.atlassian.sal.api.scheduling.PluginJob;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import jigit.indexer.JigitIndexer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

//TODO revise deprecated
public final class JigitIndexJob implements PluginJob {
    @NotNull
    private static final Logger log = LoggerFactory.getLogger(JigitIndexJob.class);

    @Override
    public void execute(@NotNull Map<String, Object> params) {
        final PluginScheduler pluginScheduler = (PluginScheduler) params.get(JigitIndexJobScheduler.PLUGIN_SCHEDULER_KEY);
        if (pluginScheduler == null) {
            log.error("JigitIndexJob::execute - pluginScheduler is null.");
            return;
        }
        final JigitIndexer jigitIndexer = (JigitIndexer) params.get(JigitIndexJobScheduler.JIGIT_INDEXER_KEY);
        if (jigitIndexer == null) {
            log.error("JigitIndexJob::execute - jigitIndexer is null.");
            return;
        }

        log.debug("Jigit index job started.");
        JigitIndexJobScheduler.unscheduleJob(pluginScheduler);

        jigitIndexer.execute();

        log.debug("Jigit index job finished.");

        JigitIndexJobScheduler.scheduleFromNow(pluginScheduler, params);
    }
}
