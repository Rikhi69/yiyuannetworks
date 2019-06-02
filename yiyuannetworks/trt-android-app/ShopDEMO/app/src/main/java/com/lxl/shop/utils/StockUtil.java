package com.lxl.shop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.aiwinn.facedetectsdk.bean.UserBean;
import com.lxl.shop.viewmodel.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * author：ajiang
 * mail：1025065158@qq.com
 * blog：http://blog.csdn.net/qqyanjiang
 */
public class StockUtil {

    public static UserModel userBean2UserModel(UserBean userBean) {
        UserModel userModel = new UserModel();
        userModel.id = userBean.id;

        userModel.name = userBean.name;
        userModel.userId = userBean.userId;
        userModel.localImagePath = userBean.localImagePath;
        userModel.urlImagePath = userBean.urlImagePath;
        userModel.compareScore = userBean.compareScore;
        userModel.features = userBean.features;
        userModel.headImage = userBean.headImage;
        userModel.serverSync = userBean.serverSync;
        return userModel;
    }

    public static UserBean userModel2userBean(UserModel userModel) {
        UserBean userBean = new UserBean();
        userBean.id = userModel.id;
        userBean.name = userModel.name;
        userBean.userId = userModel.userId;
        userBean.localImagePath = userModel.localImagePath;
        userBean.urlImagePath = userModel.urlImagePath;
        userBean.compareScore = userModel.compareScore;
        userBean.features = userModel.features;
        userBean.serverSync = userModel.serverSync;
        return userBean;
    }

    public static void showToastOnMainThread(final Context context, final CharSequence msg) {
        if (TextUtils.isEmpty(msg) || context == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Float roundedFor(Float value, int rounded) {
        BigDecimal b = new BigDecimal(value);
        value = b.setScale(rounded, BigDecimal.ROUND_HALF_UP).floatValue();//小数点后保留4位
        //异常情况
        return value;
    }

    public static String getVolUnit(float num) {
        int e = (int) Math.floor(Math.log10(num));
        if (e >= 8) {
            return "亿手";
        } else if (e >= 4) {
            return "万手";
        } else {
            return "手";
        }
    }

    /**
     * 输入单位万
     *
     * @param value
     * @return
     */
    public static String getDealValue(String value) {
        try {
            float i = Float.parseFloat(value);
            if (i > 9999) {
                return roundedFor((i / 10000f), 2) + "亿";
            }
            return value + "万";
        } catch (Exception e) {

        }
        return value + "万";
    }

    /**
     * 输入单位万
     *
     * @param value
     * @return
     */
    public static String getIntegerValue(String value) {
        if (value.contains(".")) {
            return value.split("\\.")[0];
        }
        return value;
    }

    public static float culcMaxscale(float count) {
        float max = 1;
        max = count / 127 * 5;
        return max;
    }

    public static String calculationValue(String baseStr, String addStr) {
        float v = StringUtil.toFloat(baseStr) + StringUtil.toFloat(addStr);
        return String.valueOf(roundedFor(v, 2));
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(status);
    }

    /**
     * 判断是否手机
     *
     * @param context
     * @return true:手机,false:不是
     */
    public static boolean isMoblie(Context context) {
        if (isPad(context)) {
            return false;
        }
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
//        StockUtil.showToastOnMainThread(context,"model:"+model+",manu:"+manufacturer);
        if ("AIOC".equalsIgnoreCase(model) || "rockchip".equalsIgnoreCase(manufacturer)) {
//            StockUtil.showToastOnMainThread(context,"result:"+("AIOC".equalsIgnoreCase(model) || "rockchip".equalsIgnoreCase(manufacturer)));
            return false;
        }
        return true;
    }

    /**
     * 判断是否平板设备
     *
     * @param context
     * @return true:平板,false:手机
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static void screenshot(Activity context) {
        // 获取屏幕
        View dView = context.getWindow().getDecorView();
        dView.destroyDrawingCache();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        OutputStream stream;
        String path = android.os.Environment.getExternalStorageDirectory().getPath() + File.separator;
        String currentTime = DateUtil.getCurrentTime();
        String fileName = currentTime + ".jpg";
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path + fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            stream = new FileOutputStream(file);
            bmp.compress(format, 80, stream);
            stream.flush();
            stream.close();
            StockUtil.showToastOnMainThread(context, "保存成功");
            scanPhoto(context, fileName, path + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            StockUtil.showToastOnMainThread(context, "保存失败");
        }
    }

    private static void scanPhoto(Context ctx, String imgName, String imgPath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, imgPath);
        values.put(MediaStore.Images.Media.DATE_ADDED, new Date().toString());
        values.put(MediaStore.Images.Media.TITLE, imgName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imgPath)));//path_export是你导出的文件路径
//        try {
//            MediaStore.Images.Media.insertImage(ctx.getContentResolver(), imgPath, imgName, "");
//        } catch (FileNotFoundException e) {


//            e.printStackTrace();
//        }
//        Intent mediaScanIntent = new Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File file = new File(imgFileName);
//        Uri contentUri = Uri.fromFile(file);
//        mediaScanIntent.setData(contentUri);
//        ctx.sendBroadcast(mediaScanIntent);
    }

    public static long getTotalMemory(Context c) {
        // memInfo.totalMem not supported in pre-Jelly Bean APIs.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            am.getMemoryInfo(memInfo);
            if (memInfo != null) {
                return memInfo.totalMem;
            }

        }
        return 0;
    }

    public static long getAvaiMemory(Context c){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            am.getMemoryInfo(memInfo);
            if (memInfo != null) {
                return memInfo.availMem;
            }

        }
        return 0;
    }

}
