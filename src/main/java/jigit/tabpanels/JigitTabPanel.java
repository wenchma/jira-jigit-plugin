package jigit.tabpanels;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.user.ApplicationUser;
import jigit.ao.CommitManager;
import jigit.common.JigitDateFormatter;
import jigit.settings.JigitSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class JigitTabPanel extends AbstractIssueTabPanel {
    @NotNull
    private final JigitSettingsManager settingsManager;
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final JigitDateFormatter dateFormatter;

    public JigitTabPanel(@NotNull JigitSettingsManager jigitSettingsManager,
                           @NotNull CommitManager commitManager,
                           @NotNull JigitDateFormatter dateFormatter) {
        this.settingsManager = jigitSettingsManager;
        this.commitManager = commitManager;
        this.dateFormatter = dateFormatter;
    }

    @Override
    @NotNull
    public List<IssueAction> getActions(@NotNull Issue issue, @Nullable ApplicationUser user) {
        final List<IssueAction> actions = new ArrayList<>();
        actions.add(new JigitTabAction(descriptor, commitManager, settingsManager, dateFormatter, issue));

        return actions;
    }

    @Override
    public boolean showPanel(@NotNull Issue issue, @Nullable ApplicationUser user) {
        return true;
    }
}
