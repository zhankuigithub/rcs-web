package com.longmaster.core.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTaskThreadPool {

    private ThreadPoolExecutor executor;

    public AsyncTaskThreadPool(AsyncTaskThreadPoolConfig threadPoolConfig) {
        this.executor = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
                TimeUnit.SECONDS, new ArrayBlockingQueue(8), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void submit(Runnable task) {
        this.executor.execute(() -> {
            task.run();
        });
    }

}
