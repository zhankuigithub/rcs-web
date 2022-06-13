package com.longmaster.core.threadpool;

/**
 * CPU密集型：corePoolSize = CPU核数 + 1
 * IO密集型：corePoolSize = CPU核数 * 2
 */
public class AsyncTaskThreadPoolConfig {

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

    public AsyncTaskThreadPoolConfig(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }
}
