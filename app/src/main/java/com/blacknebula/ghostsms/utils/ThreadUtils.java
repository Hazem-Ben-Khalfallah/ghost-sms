package com.blacknebula.ghostsms.utils;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {

    /**
     * Execute the given {@link Runnable} on the ui thread.
     *
     * @param runnable The runnable to execute.
     */
    public static void runOnUiThread(Runnable runnable) {
        Thread uiThread = Looper.getMainLooper().getThread();
        if (Thread.currentThread() != uiThread) new Handler(Looper.getMainLooper()).post(runnable);
        else runnable.run();
    }

    /**
     * @param millisInFuture The number of millis in the future before the call of #onFinish().
     *                       Should be greater than 1000ms and a multiple of 1000ms
     * @param onFinish       Callback fired when the time is up.
     */
    public static void delay(int millisInFuture, final Runnable onFinish) {
        countDown(millisInFuture, 1000, null, onFinish);
    }


    /**
     * @param millisInFuture    The number of millis in the future before the call of #onFinish()
     * @param countDownInterval The interval along the way to receive #onTick callbacks.
     * @param onTick            Callback fired on regular interval.
     * @param onFinish          Callback fired when the time is up.
     */
    public static void countDown(int millisInFuture, int countDownInterval, final Runnable onTick, final Runnable onFinish) {
        new CountDownTimer(millisInFuture, countDownInterval) {

            /**
             * @param millisUntilFinished The amount of time until finished.
             */
            public void onTick(long millisUntilFinished) {
                if (onTick != null) {
                    onTick.run();
                }
            }

            public void onFinish() {
                if (onFinish != null) {
                    onFinish.run();
                }
            }

        }.start();
    }
}