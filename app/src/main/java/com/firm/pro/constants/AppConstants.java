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
        public static final int NATIVE = 0;
        public static final int REWARDED_VIDEO = 1;
        public static final int BANNER = 2;
        public static final int INTERSTITIAL = 3;
        public static final int SPLASH = 4;
        public static final int MEDIA_VIDEO = 50;

        public AdType() {
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

