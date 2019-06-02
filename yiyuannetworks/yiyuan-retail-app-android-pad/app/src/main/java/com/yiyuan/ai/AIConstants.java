package com.yiyuan.ai;

import android.content.pm.PackageInfo;
import android.os.Environment;

import com.yiyuan.aiwinn.faceattendance.AttApp;

import java.io.File;

/**
 * Created by wangyu on 2019/4/10.
 */

public class AIConstants {
    //    public static final String mBaseUrl = "http://47.74.128.130:8090";
//    public static final String mBaseUrl = "http://47.88.63.122:8090";
    public static final String mBaseUrl = "http://192.168.1.166:8090";
    public static final String EXTERNAL_PATH = Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : AttApp.getContext().getFilesDir()
            .getAbsolutePath();
    public static final String AI_PATH_Folder = File.separator + "ai-client";

    public static final String AI_PATH = EXTERNAL_PATH + AI_PATH_Folder;

    public static final String AI_PATH_PHOTO_TEMP = AI_PATH + File.separator + "temp";

    public static PackageInfo packageInfo = null;

    public static final String SP_Register_CustomerId = "SP_Register_CustomerId";
    public static final String SP_Register_CustomerName = "SP_Register_CustomerName";
    public static final String SP_Register_CustomerGender = "SP_Register_CustomerGender";
    public static final String SP_Authorization = "SP_Authorization";


    public static int camerId = 1;//前后摄像头
    public static int camerDataDeg = 90;//摄像头旋转
    public static int dataDeg = 180+90;//数据分析角度
}
