package jigit.common;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
public final class CommitActionHelper {
    @SuppressWarnings("unused")
    //used in velocity
    @Nullable
    public static CommitAction parse(int id) {
        for (CommitAction action : CommitAction.values()) {
            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }
}
