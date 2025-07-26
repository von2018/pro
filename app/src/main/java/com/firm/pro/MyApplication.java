package com.firm.pro;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;

import com.anythink.core.api.ATSDK;
import com.firm.pro.utils.OkHttpUtil;
import com.firm.pro.utils.PlacementIdUtil;
import com.firm.pro.utils.SPUtils;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static MyApplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this; // 初始化单例

        //针对 Android 9（API 28）及以上版本中 WebView 的一个关键配置，主要用于解决多进程场景下 WebView 可能出现的崩溃问题。
        //Android 9及以上必须设置 判断当前设备系统版本是否为 Android 9（API 28）及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 获取当前进程的名称
            String processName = getProcessName();
            // 判断当前进程是否为应用的主进程（通过包名对比）
            if (!getPackageName().equals(processName)) {
                // 为非主进程的 WebView 设置独立的数据目录后缀
                WebView.setDataDirectorySuffix(processName);
            }
        }

        // 初始化操作（按实际项目需求添加）
        initUtilClass();  // 初始化工具类
        initTaku();       // 初始化Taku
//        initStrictMode();       // 开发环境启用严格模式
        initExceptionHandler(); // 初始化全局异常捕获
        initThirdPartyLibs();   // 初始化第三方库
        // initDatabase();       // 初始化数据库（如需要）
        // initSharedPreferences(); // 初始化SharedPreferences（如需要）
    }

    /**
     * 获取全局Application实例
     * 注意：避免在非必要场景过度使用，防止内存泄漏风险
     */
    public static synchronized MyApplication getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("MyApplication尚未初始化，请检查Manifest配置");
        }
        return sInstance;
    }

    /**
     * 获取全局Context
     * 替代Activity/Fragment的context，适用于工具类等场景
     */
    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }


    /**
     * Taku初始化
     */
    private void initTaku() {
        ATSDK.init(this, PlacementIdUtil.getAppId(this), PlacementIdUtil.getAppKey( this));
        // 应用上线前需要关闭日志功能
        ATSDK.setNetworkLogDebug(true);
        // 集成检测,上线前关闭，Logcat中过滤 anythink 的Tag
        ATSDK.integrationChecking(getApplicationContext());


    }

    /**
     * 开发环境启用严格模式，检测主线程耗时操作和潜在的内存泄漏
     */
    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            // 检测主线程磁盘操作
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // 检测网络操作（在主线程）
                    .penaltyLog()      // 违规时打印日志
                    .build());

            // 检测内存泄漏
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()   // 严重违规时崩溃（仅在开发环境）
                    .build());
        }
    }

    /**
     * 初始化全局异常捕获器
     * 生产环境下捕获未处理异常，可用于上报错误信息
     */
    private void initExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "全局未捕获异常", throwable);

            // 1. 收集错误信息（设备信息、应用版本等）
            String errorInfo = collectErrorInfo(throwable);

            // 2. 上报错误信息到服务器（实际项目中实现）
            // reportErrorToServer(errorInfo);

            // 3. 提示用户并退出应用
            handleCrash();
        });
    }

    /**
     * 初始化工具类
     */
    private void initUtilClass() {
        // 初始化SPUtils，Okhttp工具类
        SPUtils.init(this);
        // 初始化OkHttpUtil,必须在SPUtils之后初始化
        OkHttpUtil.init(this);

    }

    /**
     * 初始化第三方库（根据项目实际需求添加）
     */
    private void initThirdPartyLibs() {
        // 示例：初始化统计分析SDK
        // AnalyticsSDK.init(this);

        // 示例：初始化网络请求库
        // ApiClient.init(this);

        // 示例：初始化图片加载库
        // ImageLoader.init(this);
    }

    /**
     * 收集错误信息
     */
    private String collectErrorInfo(Throwable throwable) {
        // 实际项目中应收集：设备型号、系统版本、应用版本、错误堆栈等
        StringBuilder sb = new StringBuilder();
        sb.append("App Version: ").append(BuildConfig.VERSION_NAME).append("\n");
        sb.append("System Version: ").append(android.os.Build.VERSION.RELEASE).append("\n");
        sb.append("Error: ").append(throwable.getMessage()).append("\n");
        return sb.toString();
    }

    /**
     * 处理崩溃（提示用户并退出）
     */
    private void handleCrash() {
        // 实际项目中可显示一个崩溃提示对话框
        // 这里简化处理，直接退出应用
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "Application onTerminate");
        // 释放资源操作（如需要）
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "Application onLowMemory");
        // 低内存时释放资源
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "Application onTrimMemory: " + level);
        // 根据内存紧张程度释放资源
        if (level >= TRIM_MEMORY_MODERATE) {
            // 释放非必要资源
        }
    }
}
