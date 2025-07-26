package com.firm.pro.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * UI工具类，提供主线程切换功能
 */
public class UIUtils {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 在主线程执行任务
     */
    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 已经是主线程，直接执行
            runnable.run();
        } else {
            // 切换到主线程执行
            sHandler.post(runnable);
        }
    }
}
