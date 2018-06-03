package jigit.common;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NextPageFactory implements HeaderConsumer<Map<String, List<String>>> {
    private static final @NotNull Pattern NEXT_LINK_PATTERN = Pattern.compile(".*<(.*)>; rel=\"next\"");
    private static final @NotNull String HEADER_FIELD_NAME = "Link";
    private @NotNull NextPage nextPage;

    public NextPageFactory(@NotNull NextPage nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public void accept(@NotNull Map<String, List<String>> headerFields) {
        final List<String> fieldValues = headerFields.get(HEADER_FIELD_NAME);
        if (fieldValues != null) {
            for (String fieldValue : fieldValues) {
                final Matcher nextLinkMatcher = NEXT_LINK_PATTERN.matcher(fieldValue);
                if (nextLinkMatcher.find()) {
                    nextPage = new NextPage(nextLinkMatcher.group(1));
                    return;
                }
            }
        }
        nextPage = NextPage.EMPTY;
    }

    @NotNull
    public NextPage getNextPage() {
        return nextPage;
    }
}
