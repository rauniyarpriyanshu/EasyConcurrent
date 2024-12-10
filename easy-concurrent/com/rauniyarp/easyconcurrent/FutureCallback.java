package com.rauniyarp.easyconcurrent;

public interface FutureCallback<T> {
    /**
     * This method allow you to check completion status from thread
     *
     * @param futureGet  return output from callable
     * @param taskStatus return if task is completed or not from future
     */
    void onCompleted(T futureGet, boolean taskStatus);

    /**
     * This method allow you to check if you catch any exception with message during execution
     *
     * @param error return error message if thread execution catches any error
     */
    void onError(String error);

    /**
     * This method allow you to check if you catch any exception with message while you stop any other thread when
     * latch is enabled
     *
     * @param error return error message if thread execution catches any error
     */
    void onThreadStop(String error);

}