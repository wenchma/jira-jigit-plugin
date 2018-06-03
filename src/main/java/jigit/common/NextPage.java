package jigit.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NextPage {
    public static final @NotNull NextPage EMPTY = new NextPage(null);
    private final @Nullable String url;

    public NextPage(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getUrl() {
        return url;
    }
}
