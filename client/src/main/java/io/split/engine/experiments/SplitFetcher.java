package io.split.engine.experiments;

import io.split.engine.common.FetchOptions;

/**
 * Created by adilaijaz on 5/8/15.
 */
public interface SplitFetcher extends Runnable {
    /**
     * Forces a sync of splits, outside of any scheduled
     * syncs. This method MUST NOT throw any exceptions.
     */
    void forceRefresh(FetchOptions options);

    /**
     * Forces a sync of ALL splits, outside of any scheduled
     * syncs. This method MUST NOT throw any exceptions.
     */
    boolean fetchAll(FetchOptions options);
}
