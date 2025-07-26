package com.firm.pro.constants;

public class AppConstants {

    /**
     * SharedPreferences file name
     */
    public static class SPFileName {
        public static final String USER_INFO = "user_info";

        public SPFileName() {

        }

    }

    /**
     * Ad type
     * 广告类型
     */
    public static class AdType {
        public static final int DEFAULT = -1;
        /**
         * 原生广告类型ID
         */
        public static final int NATIVE = 0;
        /**
         * 激励视频广告类型ID
         */
        public static final int REWARDED_VIDEO = 1;
        /**
         * 横幅广告类型ID
         */
        public static final int BANNER = 2;
        /**
         * 插屏广告类型ID
         */
        public static final int INTERSTITIAL = 3;
        /**
         * 开屏广告类型ID
         */
        public static final int SPLASH = 4;
        public static final int MEDIA_VIDEO = 50;

        public AdType() {
        }
    }

    /**
     * 广告平台ID
     */
    public static class NetworkFirmId {
        /**
         * 腾讯广告网络ID
         */
        public static final int TENCENT_ADS = 8;

        /**
         * 穿山甲广告网络ID
         */
        public static final int CSJ = 15;

        /**
         * 百度联盟广告网络ID
         */
        public static final int BAIDU_UNION_ADS = 22;

        /**
         * 快手广告网络ID
         */
        public static final int KUASHOU_ADS = 28;

        public NetworkFirmId() {
        }


    }

    public static class AdDismissType {
        public static final int UNKNOWN = 0;
        public static final int NORMAL = 1;
        public static final int SKIP = 2;
        public static final int TIMEOVER = 3;
        public static final int CLICKAD = 4;
        public static final int SHOWFAILED = 99;

        public AdDismissType() {
        }
    }
}

