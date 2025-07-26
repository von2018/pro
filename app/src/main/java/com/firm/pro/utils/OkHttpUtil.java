package com.firm.pro.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.firm.pro.BuildConfig;
import com.firm.pro.constants.AppConstants;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * OkHttp网络工具类
 * 包含token管理、日志打印、基础URL配置等功能
 */
public class OkHttpUtil {
    private static final String TAG = OkHttpUtil.class.getSimpleName();
    // 默认超时时间
    private static final int DEFAULT_TIMEOUT = 10;
    // 默认基础域名
    private static final String BASE_URL = BuildConfig.BASE_URL;
    // JSON媒体类型
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mOkHttpClient;
    private Context mContext;
    // Gson实例，用于JSON转换
    private Gson mGson;

    // 私有构造方法
    private OkHttpUtil(Context context) {
        this.mContext = context.getApplicationContext();
        this.mGson = new Gson();

        // 初始化日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                // 打印网络请求日志
                Log.d(TAG, "OkHttp: " + message);
            }
        });
        // 设置日志级别，BASIC包含请求方法、URL、响应码和响应时间
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        // 初始化OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new TokenInterceptor()); // 添加token拦截器

        mOkHttpClient = builder.build();
    }

    // 静态内部类实现单例模式
    private static class SingletonHolder {
        private static OkHttpUtil INSTANCE;

        private static void init(Context context) {
            if (INSTANCE == null) {
                synchronized (OkHttpUtil.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new OkHttpUtil(context);
                    }
                }
            }
        }
    }

    /**
     * 在Application中初始化
     */
    public static void init(Context context) {
        SingletonHolder.init(context);
    }

    /**
     * 获取单例实例
     * 注意：必须先在Application中调用init方法初始化
     */
    public static OkHttpUtil getInstance() {
        if (SingletonHolder.INSTANCE == null) {
            throw new IllegalStateException("OkHttpUtil not initialized! Please call init() in your Application class first.");
        }
        return SingletonHolder.INSTANCE;
    }

    /**
     * Token拦截器，自动为请求添加token
     */
    private class TokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            // 从本地获取token，这里使用SharedPreferences示例

            String token = SPUtils.getInstance().getString(AppConstants.SPFileName.USER_INFO, "token", "");
            // 如果token不为空，则为请求添加Authorization头
            if (!TextUtils.isEmpty(token)) {
                Request authorisedRequest = originalRequest.newBuilder()
                        .header("token", token)
                        .build();
                return chain.proceed(authorisedRequest);
            }

            return chain.proceed(originalRequest);
        }
    }

    /**
     * 拼接完整URL
     * @param url 可以是完整URL或相对路径
     * @return 完整的请求URL
     */
    private String getUrl(String url) {
        // 如果是完整URL则直接使用，否则拼接基础URL
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            return BASE_URL + url;
        }
    }

    /**
     * GET请求
     * @param url 请求地址（可以是相对路径）
     * @param params 请求参数
     * @param callback 回调接口
     */
    public void get(String url, Map<String, String> params, final HttpCallback callback) {
        // 拼接URL和参数
        StringBuilder urlBuilder = new StringBuilder(getUrl(url));
        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            // 移除最后一个&
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        // 创建请求
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .build();
        // 发起异步请求
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 在主线程回调
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                // 在主线程回调
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (response.isSuccessful()) {
                                callback.onSuccess(responseData);
                            } else {
                                callback.onFailure("请求失败，错误码：" + response.code());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * POST表单请求
     * @param url 请求地址（可以是相对路径）
     * @param params 请求参数
     * @param callback 回调接口
     */
    public void postForm(String url, Map<String, String> params, final HttpCallback callback) {
        // 构建表单请求体
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBuilder.build();

        // 创建请求
        Request request = new Request.Builder()
                .url(getUrl(url))
                .post(requestBody)
                .build();

        // 发起异步请求
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (response.isSuccessful()) {
                                callback.onSuccess(responseData);
                            } else {
                                callback.onFailure("请求失败，错误码：" + response.code());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * POST JSON请求
     * @param url 请求地址（可以是相对路径）
     * @param json 请求的JSON字符串
     * @param callback 回调接口
     */
    public void postJson(String url, String json, final HttpCallback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(getUrl(url))
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (response.isSuccessful()) {
                                callback.onSuccess(responseData);
                            } else {
                                callback.onFailure("请求失败，错误码：" + response.code());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 将对象转换为JSON字符串并发送POST请求
     * @param url 请求地址
     * @param obj 要转换为JSON的对象
     * @param callback 回调接口
     */
    public void postObject(String url, Object obj, HttpCallback callback) {
        String json = mGson.toJson(obj);
        postJson(url, json, callback);
    }

    /**
     * 网络请求回调接口
     */
    public interface HttpCallback {
        void onSuccess(String response);
        void onFailure(String errorMsg);
    }
}
