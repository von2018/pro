package com.firm.pro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * 多文件SharedPreferences工具类
 * 支持操作不同名称的SP文件，线程安全且避免内存泄漏
 */
public class SPUtils {
    // 单例实例
    private static volatile SPUtils instance;
    // 全局Context（Application级别，避免内存泄漏）
    private Context mContext;

    // 私有构造方法，防止外部实例化
    private SPUtils(Context context) {
        // 持有Application Context，避免Activity Context导致的内存泄漏
        this.mContext = context.getApplicationContext();
    }

    /**
     * 初始化工具类（必须在Application中调用）
     * @param context 建议传入Application实例
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (SPUtils.class) {
                if (instance == null) {
                    instance = new SPUtils(context);
                }
            }
        }
    }

    /**
     * 获取单例实例（需先调用init()）
     */
    public static SPUtils getInstance() {
        if (instance == null) {
            throw new RuntimeException("SPUtils未初始化，请在Application中调用init()");
        }
        return instance;
    }

    /**
     * 根据文件名获取SharedPreferences实例
     * @param spName SP文件名（如"user_info"、"app_config"）
     */
    private SharedPreferences getSp(String spName) {
        if (TextUtils.isEmpty(spName)) {
            throw new IllegalArgumentException("SP文件名不能为空");
        }
        // 模式默认使用MODE_PRIVATE（仅当前应用可访问）
        return mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    // ========== 存储方法 ==========

    /**
     * 存储String类型数据
     * @param spName SP文件名
     * @param key 键
     * @param value 值
     */
    public void putString(String spName, String key, String value) {
        Editor editor = getSp(spName).edit();
        editor.putString(key, value);
        editor.apply(); // 异步提交（推荐，不阻塞主线程）
    }

    /**
     * 存储int类型数据
     */
    public void putInt(String spName, String key, int value) {
        Editor editor = getSp(spName).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 存储boolean类型数据
     */
    public void putBoolean(String spName, String key, boolean value) {
        Editor editor = getSp(spName).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 存储float类型数据
     */
    public void putFloat(String spName, String key, float value) {
        Editor editor = getSp(spName).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * 存储long类型数据
     */
    public void putLong(String spName, String key, long value) {
        Editor editor = getSp(spName).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 存储Set<String>类型数据
     */
    public void putStringSet(String spName, String key, Set<String> value) {
        Editor editor = getSp(spName).edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    // ========== 获取方法 ==========

    /**
     * 获取String类型数据
     * @param defaultValue 默认值（当key不存在时返回）
     */
    public String getString(String spName, String key, String defaultValue) {
        return getSp(spName).getString(key, defaultValue);
    }

    /**
     * 获取int类型数据
     */
    public int getInt(String spName, String key, int defaultValue) {
        return getSp(spName).getInt(key, defaultValue);
    }

    /**
     * 获取boolean类型数据
     */
    public boolean getBoolean(String spName, String key, boolean defaultValue) {
        return getSp(spName).getBoolean(key, defaultValue);
    }

    /**
     * 获取float类型数据
     */
    public float getFloat(String spName, String key, float defaultValue) {
        return getSp(spName).getFloat(key, defaultValue);
    }

    /**
     * 获取long类型数据
     */
    public long getLong(String spName, String key, long defaultValue) {
        return getSp(spName).getLong(key, defaultValue);
    }

    /**
     * 获取Set<String>类型数据
     */
    public Set<String> getStringSet(String spName, String key, Set<String> defaultValue) {
        return getSp(spName).getStringSet(key, defaultValue);
    }

    // ========== 其他常用方法 ==========

    /**
     * 移除某个key
     */
    public void remove(String spName, String key) {
        Editor editor = getSp(spName).edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清空某个SP文件的所有数据
     */
    public void clear(String spName) {
        Editor editor = getSp(spName).edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 判断某个key是否存在
     */
    public boolean contains(String spName, String key) {
        return getSp(spName).contains(key);
    }

    /**
     * 获取某个SP文件的所有键值对
     */
    public Map<String, ?> getAll(String spName) {
        return getSp(spName).getAll();
    }
}
