package jigit.common;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public enum APIHelper {
    ;
    @NotNull
    private static final String ENCODING = "UTF-8";

    public static @NotNull String encode(@NotNull String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, APIHelper.ENCODING);
    }
}
