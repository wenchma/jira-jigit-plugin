package jigit.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PageParam {
    @NotNull
    private static final String PER_PAGE_PARAM_NAME = "per_page";
    @NotNull
    private static final String PAGE_PARAM_NAME = "page";
    private static final int PER_PAGE_MAX_VALUE = 100;
    private static final int PAGE_DEFAULT_VALUE = 1;
    public static final @NotNull PageParam MAX = new PageParam(PER_PAGE_MAX_VALUE);

    private final int page;
    private final int perPage;

    public PageParam(int page, int perPage) {
        this.page = page;
        this.perPage = perPage;
    }

    public PageParam(int perPage) {
        this(PAGE_DEFAULT_VALUE, perPage);
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PageParam)) return false;

        final PageParam pageParam = (PageParam) obj;

        return page == pageParam.page && perPage == pageParam.perPage;
    }

    @Override
    public int hashCode() {
        int result = page;
        result = 31 * result + perPage;
        return result;
    }

    @Override
    public @NotNull String toString() {
        return PAGE_PARAM_NAME + "=" + page + "&" + PER_PAGE_PARAM_NAME + "=" + perPage;
    }
}
