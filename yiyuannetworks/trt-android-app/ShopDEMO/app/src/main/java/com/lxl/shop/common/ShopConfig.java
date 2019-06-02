package com.lxl.shop.common;

import com.lxl.shop.utils.StockUtil;

/**
 * Created by yanglei on 2018/11/24.
 */

public class ShopConfig {

    public static int AD_SHOW_TIME_INTERVAL = 10 * 1000;//广告间隔时间

    public static int WELCOME_SHOW_TIME = 10 * 1000;//欢迎XX展示时间

    public static int SYNC_CUSTOMER_TIME_INTERVAL = 30 * 1000;//后台发服务的间隔时间

    public static int SYNC_CUSTOMER_VISIT_TIME_INTERVAL = 5 * 1000;//轮训请求用户进店时间

    public static int SHOW_VIEW_PAGER_DELAY = 10000;//N秒无操作无识别，则恢复广告界面


    public static int RECOGNITION_DELAY_TIME_MAX = 2000;//N秒无操作无识别，则恢复广告界面
    public static int RECOGNITION_DELAY_TIME = 100;//N秒无操作无识别，则恢复广告界面
    public static int DELETE_DELAY_TIME = 120;

    public static String strIp = "192.168.1.18";

    public static String count = "5";
    public static String USER_NAME = "admin";
    public static String PASS_WORD = "Admin123";
    public static int PORT = 80;

    public static boolean isSupportCamera;//
    public static boolean isSupportSDCard;//
    public static boolean isSupportVideo;//

    public static boolean isPhone;


    public static int getCount() {
        try {
            int i = Integer.parseInt(count);
            if (i <= 1) {
                i = 1;
            }
            return i;
        } catch (Exception e) {

        }
        return 1;
    }

}
