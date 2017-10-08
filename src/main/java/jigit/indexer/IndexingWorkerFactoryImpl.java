package jigit.indexer;

import jigit.ao.CommitManager;
import jigit.ao.QueueItemManager;
import jigit.indexer.api.APIAdapter;
import jigit.indexer.api.APIAdapterFactory;
import jigit.settings.JigitRepo;
import org.jetbrains.annotations.NotNull;

public final class IndexingWorkerFactoryImpl implements IndexingWorkerFactory {
    @NotNull
    private final APIAdapterFactory apiAdapterFactory;
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

    public IndexingWorkerFactoryImpl(@NotNull APIAdapterFactory apiAdapterFactory,
                                     @NotNull QueueItemManager queueItemManager,
                                     @NotNull PersistStrategyFactory persistStrategyFactory,
                                     @NotNull CommitManager commitManager,
                                     @NotNull RepoDataCleaner repoDataCleaner,
                                     @NotNull IssueKeysExtractor issueKeysExtractor) {
        this.apiAdapterFactory = apiAdapterFactory;
        this.queueItemManager = queueItemManager;
        this.persistStrategyFactory = persistStrategyFactory;
        this.commitManager = commitManager;
        this.repoDataCleaner = repoDataCleaner;
        this.issueKeysExtractor = issueKeysExtractor;
    }

    @Override
    @NotNull
    public IndexingWorker build(@NotNull JigitRepo repo) {
        final APIAdapter apiAdapter = apiAdapterFactory.getAPIAdapter(repo);
        return new IndexingWorker(apiAdapter, repo,
                new DeletingForcePushHandler(commitManager, apiAdapter, repoDataCleaner),
                queueItemManager, persistStrategyFactory, issueKeysExtractor);
    }
}
