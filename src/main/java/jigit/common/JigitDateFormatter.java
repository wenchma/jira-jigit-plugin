package jigit.common;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
//all methods are used in velocity view
public final class JigitDateFormatter {
    @NotNull
    private final ThreadLocal<SimpleDateFormat> dateFormatter;

    public JigitDateFormatter(@NotNull ApplicationProperties applicationProperties) {
        final String dateTimeFormat = applicationProperties.asMap().get(APKeys.JIRA_LF_DATE_COMPLETE).toString();
        this.dateFormatter = new ThreadLocal<SimpleDateFormat>() {
            @NotNull
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(dateTimeFormat);
            }
        };
    }

    @NotNull
    public String format(@NotNull Date date) {
        return dateFormatter.get().format(date);
    }
}
