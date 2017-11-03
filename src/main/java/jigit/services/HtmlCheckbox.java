package jigit.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HtmlCheckbox {
    private static final @NotNull String CHECKED_VALUE = "on";
    private final boolean checked;

    public HtmlCheckbox(@Nullable String value) {
        this.checked = CHECKED_VALUE.equals(value);
    }

    public boolean isChecked() {
        return checked;
    }
}
