package com.firm.pro.ad;

import android.content.Context;
import android.util.Log;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.firm.pro.constants.AppConstants;

import java.util.Map;

/**
 * 激励视频广告管理器
 */
public class RewardedVideoAdManager extends BaseAdManager {
    private ATRewardVideoAd mRewardVideoAd;
    private RewardedAdListener mRewardedListener;

    public RewardedVideoAdManager(Context context, String placementKey) {
        super(context, placementKey);
    }

    @Override
    protected String getPlacementIdByKey(String key) {
        Map<String, String> map = getPlacementMap(AppConstants.AdType.REWARDED_VIDEO);
        return map != null ? map.get(key) : "";
    }

    @Override
    public void loadAd() {
        if (!checkContextValid() || !checkPlacementValid() || isLoading) {
            return;
        }

        isLoading = true;
        currentState = AdState.LOADING;
        mRewardVideoAd = new ATRewardVideoAd(mContext, mPlacementId);
        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {

            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

            }

            @Override
            public void onReward(ATAdInfo atAdInfo) {

            }
        });

        mRewardVideoAd.load();
    }

    @Override
    public boolean showAd() {
        if (!isAdReady() || mRewardVideoAd == null) {
            return false;
        }
        return mRewardVideoAd.show(mContext);
    }

    @Override
    public void destroyAd() {
        if (mRewardVideoAd != null) {
//            mRewardVideoAd.destroy();
            mRewardVideoAd = null;
        }
        mRewardedListener = null;
        mStatusListener = null;
        currentState = AdState.IDLE;
        isAdLoaded = false;
    }

    /**
     * 设置激励视频专用监听器
     */
    public void setRewardedAdListener(RewardedAdListener listener) {
        this.mRewardedListener = listener;
        super.setAdStatusListener(listener);
    }

    /**
     * 激励视频广告监听器
     */
    public interface RewardedAdListener extends AdStatusListener {
        void onRewardGranted();
        void onAdClicked();
    }
}
