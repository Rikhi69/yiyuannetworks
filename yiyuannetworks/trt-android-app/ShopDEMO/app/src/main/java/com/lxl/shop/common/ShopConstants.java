package com.lxl.shop.common;

import android.os.Environment;

import com.lxl.shop.AttApp;

import java.io.File;

/**
 * Created by yanglei on 2018/11/24.
 */

public class ShopConstants {

    public static final String CUSTOMERID = "customerID";
    public static final String CUSTOMER_MODEL = "customerModel";
    public static final int RegisterCode = 101;

    public static final String USER_BROADCAST = "com.lxl.shop.USER_BROADCAST";
    public static final String CATCH_CUSTOMER = "com.lxl.shop.CATCHCUSTOMER_BROADCAST";

    public static final String CUSTOMER_RECENT_LIST = "customerList";
    public static final String CATCH_CUSTOMER_PATH = "CATCH_CUSTOMER_PATH";

    public static final String SENDER_RESULT_FAIL = "error";

    public static final String SP_Register_CustomerId = "SP_Register_CustomerId";
    public static final String SP_Register_CustomerName = "SP_Register_CustomerName";
    public static final String SP_Register_CustomerGender = "SP_Register_CustomerGender";
    public static final String SP_Authorization = "SP_Authorization";

    public static final String SD_RECOGNITION_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "yy";//人脸识别后存放的目录

    public static final String EXTERNAL_PATH = Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : AttApp.getContext().getFilesDir()
            .getAbsolutePath();
    public static final String ASHOP_PATH = EXTERNAL_PATH + File.separator + "ashop";
    public static final String TEMP_PATH = ASHOP_PATH + File.separator + "temp";
    public static final String CONFIG_PATH = ASHOP_PATH + File.separator + "config.txt";
    public static final String CRASH_PATH = ASHOP_PATH + File.separator + "crash";

}
