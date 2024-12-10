package com.rauniyarp.easyconcurrent;

import java.util.concurrent.*;
/**
 * This class helps you to create an easy concurrent support
 * It uses ThreadPoolExecutor, Future and CountDownLatch to work
 * @author Priyanshu Rauniyar
 * @version 1
 * @see Future
 * @see ThreadPoolExecutor
 * @see CountDownLatch
* */
public class FutureApi<T> {
    private boolean isLatchEnabled;
    private FutureCallback<T> mCallback;
    private ThreadPoolExecutor poolExecutor;
    private Future<T> mFuture;
    private CountDownLatch mCountDownLatch;
    private Callable<T> mCallable;

    /**
     * Initialize Future Api
     * @param callable use callable interface with your generic type
     * @param executor {@link ThreadPoolExecutor} instance
     * @param latchAccess uses {@link CountDownLatch} to stop thread if you set true you can block another thread until FutureApi complete its task
     *                    by using {@link FutureApi#stopAnotherThread()}
    * */
    public FutureApi(Callable<T> callable, ThreadPoolExecutor executor, boolean latchAccess) {
        mCallable = callable;
        if (executor != null) {
            poolExecutor = executor;
        } else {
            poolExecutor = getDefaultThreadPool();
        }
        isLatchEnabled = latchAccess;

    }

    /**
     * Initialize Future Api with main constructor{@link #FutureApi(Callable, ThreadPoolExecutor, boolean)}
     * @param callable use callable interface with your generic type
     * uses single {@link ThreadPoolExecutor}
     * By Default latch is disabled
     * */
    public FutureApi(Callable<T> callable) {
        this(callable, getDefaultThreadPool(), false);
    }

    /**
     * Initialize Future Api with main constructor{@link #FutureApi(Callable, ThreadPoolExecutor, boolean)}
     * @param callable use callable interface with your generic type
     * uses single {@link ThreadPoolExecutor}
     *  @param latchAccess uses {@link CountDownLatch} to stop thread if you set true you can block another thread until FutureApi complete its task
     *              by using {@link FutureApi#stopAnotherThread()}
     * */
    public FutureApi(Callable<T> callable, boolean latchAccess) {
        this(callable, getDefaultThreadPool(), latchAccess);
    }

    public static ThreadPoolExecutor getDefaultThreadPool() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }


    /**
     * set callback to track event in your app for this Api
     * @param  listener-callback or listener for this api use<br> <code>new Listener< GENERIC TYPE >{and methods}</T></code>
    * */
    public void setListener(FutureCallback<T> listener) {
        mCallback = listener;
    }

    private void initApp() {
        if (poolExecutor == null) {
            poolExecutor = getDefaultThreadPool();
        }
        if (isLatchEnabled) {
            mCountDownLatch = new CountDownLatch(1);
        }
    }

    /**
     * Start execution process here, must call {@link FutureApi#setListener(FutureCallback)}
     * to start execution
     */
    public void execute() {
        initApp();
        try {
            mFuture = poolExecutor.submit(mCallable);
            T result = mFuture.get();
            mCallback.onCompleted(result, mFuture.isDone());
        } catch (ExecutionException | InterruptedException e) {
            mCallback.onError(e.toString());
        } finally {
            if (isLatchEnabled) {
                mCountDownLatch.countDown();
            }
        }
    }

    /**
     * Use this method when you want to block another thread must enabled latch
     *
     * @see FutureApi#FutureApi(Callable, boolean)
     */
    public void stopAnotherThread() {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            mCallback.onThreadStop(e.getMessage());
        }
    }



    /**
     * This method allow you to stop thread
     */
    public void shutDown() {
        poolExecutor.shutdown();
    }

    /**
     * This method allow you to shut down thread with time
     *
     * @param time value of time
     * @param unit format the time in unit i. e time is 1 you can choose{@link TimeUnit#SECONDS} if you want 1 second
     * @see TimeUnit know more about TimeUnit here
     */
    public void shutDown(long time, TimeUnit unit) {
        try {
            boolean z = poolExecutor.awaitTermination(time, unit);
            poolExecutor.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This method allow you to shut down thread by forcing it to shut down
     */
    public void shutDownNow() {
        poolExecutor.shutdownNow();
    }

   /* public void reuse(Callable<T> callable, ThreadPoolExecutor executor, boolean latchAccess) {
        mCallable = callable;
        poolExecutor = executor;
        isLatchEnabled = latchAccess;
    }

    public void reuse(Callable<T> callable, ThreadPoolExecutor executor) {
        reuse(callable, executor, false);
        // isLatchEnabled = latchAccess;
    }

    public void reuse(Callable<T> callable) {
        reuse(callable, getDefaultThreadPool(), false);
        System.out.println();
    }

    public void reuse(Callable<T> callable, boolean latchAccess) {
        reuse(callable, getDefaultThreadPool(), latchAccess);
    }*/


    /**
     * This method clear all associated resources while using this Api
     */
    public void clear() {
        mCallable = null;
        mFuture = null;
        mCountDownLatch = null;
        mCallback = null;
    }
}

