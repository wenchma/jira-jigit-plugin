package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.indexer.repository.RepoInfo;
import jigit.indexer.repository.RepoInfoFactory;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public final class IndexingWorkerFactoryImpl implements IndexingWorkerFactory {
    @NotNull
    private final RepoInfoFactory repoInfoFactory;
    @NotNull
    private final QueueItemManager queueItemManager;
    @NotNull
    private final PersistStrategyFactory persistStrategyFactory;
    @NotNull
    private final CommitManager commitManager;
    @NotNull
    private final RepoDataCleaner repoDataCleaner;
    @NotNull
    private final IssueKeysExtractor issueKeysExtractor;

    public IndexingWorkerFactoryImpl(@NotNull RepoInfoFactory repoInfoFactory,
                                     @NotNull QueueItemManager queueItemManager,
                                     @NotNull PersistStrategyFactory persistStrategyFactory,
                                     @NotNull CommitManager commitManager,
                                     @NotNull RepoDataCleaner repoDataCleaner,
                                     @NotNull IssueKeysExtractor issueKeysExtractor) {
        this.repoInfoFactory = repoInfoFactory;
        this.queueItemManager = queueItemManager;
        this.persistStrategyFactory = persistStrategyFactory;
        this.commitManager = commitManager;
        this.repoDataCleaner = repoDataCleaner;
        this.issueKeysExtractor = issueKeysExtractor;
    }

    @Override
    @NotNull
    public Collection<IndexingWorker> build(@NotNull JigitRepo repo) throws IOException {
        final Collection<IndexingWorker> workers = new ArrayList<>();
        for (RepoInfo repoInfo : repoInfoFactory.build(repo)) {
            workers.add(new IndexingWorker(repoInfo,
                    new DeletingForcePushHandler(commitManager, repoInfo, repoDataCleaner),
                    queueItemManager, persistStrategyFactory, issueKeysExtractor));

        }
        return workers;
    }
}
