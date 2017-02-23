package jigit.common;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
public final class CommitDateHelper {
    @NotNull
    private static final ThreadLocal<SimpleDateFormat> formatterUTC = new ThreadLocal<SimpleDateFormat>() {
        @NotNull
        @Override
        protected SimpleDateFormat initialValue() {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat;
        }
    };
    @NotNull
    private static final ThreadLocal<SimpleDateFormat> formatterLocal = new ThreadLocal<SimpleDateFormat>() {
        @NotNull
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };

    @NotNull
    public static Date toUTC(@NotNull Date date) throws ParseException {
        final String formatted = formatterUTC.get().format(date);
        return formatterLocal.get().parse(formatted);
    }

    @SuppressWarnings("unused")
    //used in velocity templates
    @NotNull
    public static Date toLocal(@NotNull Date date) throws ParseException {
        final String formatted = formatterLocal.get().format(date);
        return formatterUTC.get().parse(formatted);
    }
}
