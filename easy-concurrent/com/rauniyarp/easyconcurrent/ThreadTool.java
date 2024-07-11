package com.rauniyarp.easyconcurrent;

import java.util.concurrent.*;

/**
 * This class helps you to create an easy concurrent support
 * It uses ThreadPoolExecutor and CountDownLatch to work
 * @author Priyanshu Rauniyar
 * @version 1
 * @see ThreadPoolExecutor
 * @see CountDownLatch
 * */
public abstract class ThreadTool {
    private String threadName;
    private ThreadPoolExecutor executor;
    private CountDownLatch latch;
    private boolean latchEnabled = false;
    /**
     * by default Thread Name will be ThreadTool
     * uses default {@link ThreadPoolExecutor} by {@link ThreadTool}
     * */
    public ThreadTool() {
        this("ThreadTool", new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ThreadTool");
            }
        }));
    }

    /**
     * @param threadName set Thread Name
     * uses default {@link ThreadPoolExecutor} by {@link ThreadTool}
     * */
    public ThreadTool(String threadName) {
        this(threadName, new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadName);
            }
        }));
    }

    /**
     * @param threadName set Thread Name
     * @param executor set {@link ThreadPoolExecutor}
     * */
    public ThreadTool(String threadName, ThreadPoolExecutor executor) {
        this.threadName = threadName;
        this.executor = executor;
    }

 /**
  * @return a single ThreadPoolExecutor
  * */
    public static ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String name) {
        threadName = name;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }


    /**
     * @param latchEnabled if you set true you can use method {@link ThreadTool#stopCurrentThread()}
     * @see ThreadTool#stopCurrentThread()
     * */
    public void setLatchEnabled(boolean latchEnabled) {
        this.latchEnabled = latchEnabled;
        if (latchEnabled){
            latch=new CountDownLatch(1);
        }
    }

    /**
     * before thread started
     * use this when you want to initialize something before thread start
    * */
    public abstract void beforeThreadStarted();
    /**
     * on thread started
     * use this when you want to do something on thread start like loading text from url
     * */
    public abstract void onThreadStarted();
    /**
     * on thread catch error
     * use this when you want to catch error if thread catch any exception
     * */
    public abstract void onThreadCatchedError(String err);

    private void start() {
        beforeThreadStarted();
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    onThreadStarted();
                }
            });
        } catch (Exception error) {
            onThreadCatchedError(error.toString());
        } finally {
            if (latchEnabled) {
                latch.countDown();
            }
        }
    }

    /**
     * stop current means you can use this method to stop another thread and until {@link ThreadTool} has completed its task
     * */
    public void stopCurrentThread() {
        try {
            if (latchEnabled) {
                latch.await();
            }
        } catch (InterruptedException e) {
            onThreadCatchedError(e.toString());
        }
    }

    /**
     * starts execution of your task
     * */
    public ThreadTool startExecution() {
        start();
        return this;
    }

    /**
     * shutdown thread with timer
     * @param time represent time in long
     * @param unit represent time unit type example TimeUnit.MILISECOND SECOND and so on
    * */
    public void shutDownThread(long time, TimeUnit unit) {
        try {
            boolean z = executor.awaitTermination(time, unit);
            executor.shutdown();
        } catch (InterruptedException e) {
            onThreadCatchedError(e.toString());
        }
    }

    public void normalShutDown() {
        shutDownThread(1, TimeUnit.SECONDS);
    }

}
