package com.firm.pro.ad;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.firm.pro.constants.AppConstants;
import com.firm.pro.utils.PlacementIdUtil;
import com.firm.pro.utils.UIUtils;

import java.util.Map;

/**
 * 基础广告管理器，封装通用广告功能
 */
public abstract class BaseAdManager {
    protected static final String TAG = "AdManager";
    protected Context mContext;
    protected String mPlacementId;
    protected AdStatusListener mStatusListener;
    protected boolean isLoading;
    protected boolean isAdLoaded;

    // 广告状态枚举
    public enum AdState {
        IDLE, LOADING, LOADED, FAILED, SHOWING, CLOSED
    }

    protected AdState currentState = AdState.IDLE;

    public BaseAdManager(Context context, String placementKey) {
        this.mContext = context.getApplicationContext();
        this.mPlacementId = getPlacementIdByKey(placementKey);
    }

    /**
     * 根据广告类型和key获取placementId
     */
    protected abstract String getPlacementIdByKey(String key);

    /**
     * 加载广告
     */
    public abstract void loadAd();

    /**
     * 展示广告
     * @return 是否展示成功
     */
    public abstract boolean showAd();

    /**
     * 销毁广告资源
     */
    public abstract void destroyAd();

    /**
     * 检查广告是否可展示
     */
    public boolean isAdReady() {
        return isAdLoaded && currentState != AdState.SHOWING;
    }

    /**
     * 设置广告状态监听器
     */
    public void setAdStatusListener(AdStatusListener listener) {
        this.mStatusListener = listener;
    }

    /**
     * 通用状态回调处理
     */
    protected void notifyAdLoaded() {
        currentState = AdState.LOADED;
        isAdLoaded = true;
        isLoading = false;
        if (mStatusListener != null) {
            UIUtils.runOnUiThread(() -> mStatusListener.onAdLoaded());
        }
    }

    protected void notifyAdFailed(String errorMsg) {
        currentState = AdState.FAILED;
        isAdLoaded = false;
        isLoading = false;
        Log.e(TAG, "Ad failed: " + errorMsg);
        if (mStatusListener != null) {
            UIUtils.runOnUiThread(() -> mStatusListener.onAdFailed(errorMsg));
        }
    }

    protected void notifyAdShown() {
        currentState = AdState.SHOWING;
        if (mStatusListener != null) {
            UIUtils.runOnUiThread(() -> mStatusListener.onAdShown());
        }
    }

    protected void notifyAdClosed() {
        currentState = AdState.CLOSED;
        if (mStatusListener != null) {
            UIUtils.runOnUiThread(() -> mStatusListener.onAdClosed());
        }
    }

    /**
     * 广告状态监听接口
     */
    public interface AdStatusListener {
        void onAdLoaded();
        void onAdFailed(String errorMsg);
        void onAdShown();
        void onAdClosed();
    }

    /**
     * 获取广告类型对应的所有placementId映射
     */
    protected Map<String, String> getPlacementMap(int adType) {
        switch (adType) {
            case AppConstants.AdType.REWARDED_VIDEO:
                return PlacementIdUtil.getRewardedVideoPlacements(mContext);
            case AppConstants.AdType.INTERSTITIAL:
                return PlacementIdUtil.getInterstitialPlacements(mContext);
            case AppConstants.AdType.BANNER:
                return PlacementIdUtil.getBannerPlacements(mContext);
            case AppConstants.AdType.SPLASH:
                return PlacementIdUtil.getSplashPlacements(mContext);
            case AppConstants.AdType.NATIVE:
                return PlacementIdUtil.getNativeSelfrenderPlacements(mContext);
            default:
                return null;
        }
    }

    /**
     * 检查上下文有效性
     */
    protected boolean checkContextValid() {
        if (mContext == null) {
            Log.e(TAG, "Context is null");
            return false;
        }
        return true;
    }

    /**
     * 检查placementId有效性
     */
    protected boolean checkPlacementValid() {
        if (TextUtils.isEmpty(mPlacementId)) {
            notifyAdFailed("Invalid placement id");
            return false;
        }
        return true;
    }
}
