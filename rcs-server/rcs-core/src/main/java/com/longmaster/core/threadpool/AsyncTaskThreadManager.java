package com.longmaster.core.threadpool;

public class AsyncTaskThreadManager {

    private static volatile AsyncTaskThreadManager single;

    private AsyncTaskThreadPool threadPool;

    private AsyncTaskThreadManager() {
        AsyncTaskThreadPoolConfig config = new AsyncTaskThreadPoolConfig(12, 20, 10);
        this.threadPool = new AsyncTaskThreadPool(config);
    }

    public static AsyncTaskThreadManager getInstance() {

        if (single == null) {
            synchronized (AsyncTaskThreadManager.class) {
                if (single == null) {
                    single = new AsyncTaskThreadManager();
                }
            }
        }
        return single;
    }

    public void submit(Runnable task) {
        this.threadPool.submit(task);
    }

}
