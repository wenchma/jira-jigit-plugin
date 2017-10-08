package jigit.indexer;

import com.atlassian.jira.util.JiraKeyUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JiraIssueKeysExtractor implements IssueKeysExtractor {
    @NotNull
    @Override
    public List<String> extract(@NotNull String text) {
        return JiraKeyUtils.getIssueKeysFromString(text);
    }
}
