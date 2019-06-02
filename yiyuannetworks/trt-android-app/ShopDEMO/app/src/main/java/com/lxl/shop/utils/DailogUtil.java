package com.lxl.shop.utils;


import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.lxl.shop.AttApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/2/28
 */
public class DailogUtil {

    private static List<String> welcomeList = new ArrayList<>();
    private static AlertDialog alert = null;
    private static long timeBegin = 0;

    public static void showDailog(Context context, String text, final long time){
        if(alert == null){
            alert = new AlertDialog.Builder(context).create();
        }
        welcomeList.add(text);
        timeBegin = new Date().getTime();
        alert.setTitle("访客提示:");
        String s = "";
        for (String str : welcomeList) {
            s += str + "\r\n";
        }
        alert.setMessage(s);
        alert.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(new Date().getTime() - timeBegin > time){
                    welcomeList.clear();
                    alert.dismiss();
                }
            }
        }, 500);
    }
}
