package com.ericsson.lte.session.utils;

import java.util.concurrent.*;

public class SessionEventPool {
    private ThreadPoolExecutor threadPool;

    private SessionEventPool() {
        this(1, 1, 1, TimeUnit.SECONDS);
    }

    private SessionEventPool(int corePoolSize, int maxnumPoolSize, long keepAlive, TimeUnit unit) {
        LinkedBlockingQueue<Runnable> linkedQueue = new LinkedBlockingQueue<>();
        threadPool = new ThreadPoolExecutor(corePoolSize, maxnumPoolSize, keepAlive, unit, linkedQueue);
        threadPool.prestartAllCoreThreads();
    }

    static class SessionEventHolder {
        private static final SessionEventPool INSTANCE = new SessionEventPool();
    }

    private static class SingleTonPool {
        private static final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(200), new ThreadPoolExecutor.AbortPolicy());
    }

    public static ExecutorService getSingleTonPool() {
        return SingleTonPool.pool;
    }

    public static SessionEventPool getTaskPool() {
        return SessionEventHolder.INSTANCE;
    }

    public Future submit(Callable task) {
        return this.threadPool.submit(task);
    }
}
