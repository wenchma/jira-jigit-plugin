package jigit.indexer.api;

import jigit.common.CommitAction;
import org.jetbrains.annotations.NotNull;

final class CommitFileAdapterStub implements CommitFileAdapter {
    private final int idx;

    public CommitFileAdapterStub(int idx) {
        this.idx = idx;
    }

    @NotNull
    @Override
    public String getNewPath() {
        return "folderNew" + idx + "/MyClass" + idx + ".java";
    }

    @NotNull
    @Override
    public String getOldPath() {
        return "folderOld" + idx + "/MyClass" + idx + ".java";
    }

    @NotNull
    @Override
    public CommitAction getCommitAction() {
        return CommitAction.MODIFIED;
    }
}
