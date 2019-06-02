package com.lxl.shop.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lxl.shop.AttApp;
import com.lxl.shop.common.ShopConstants;

import java.io.File;
import java.io.IOException;

/**
 * Created by xiangleiliu on 2017/8/23.
 */
public class LogUtil {
    final static String TAG = "lxltest";
    private static LogUtil instance;
    private static Context mContext;
    private static String shop = "ashop.txt";

    private LogUtil() {

    }

    public static synchronized LogUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LogUtil();
            mContext = AttApp.getContext();
        }
        return instance;
    }

    public static synchronized LogUtil getInstance() {
        return getInstance(null);
    }

    public void logE(String message) {
        record("E", message);
    }

    public void LogW(String message) {
        record("W", message);
    }


    public void record(String level, String message) {
        if ("E".equals(level)) {
            Log.e(TAG, level + ":" + message);
        } else {
            Log.w(TAG, level + ":" + message);
        }
        if (mContext != null) {
            String currentTime = DateUtil.getCurrentTime();
            File file = new File(ShopConstants.ASHOP_PATH + File.separator + shop);
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            IOHelper.writerStrByCodeToFile(file, "utf-8", true, currentTime + ";" + level + ";" + message + "\n");
        }
    }

}
