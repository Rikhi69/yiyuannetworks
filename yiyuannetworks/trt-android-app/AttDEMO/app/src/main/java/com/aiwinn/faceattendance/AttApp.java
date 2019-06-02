package com.aiwinn.faceattendance;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;

import com.aiwinn.base.AiwinnManager;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.Status;

/**
 * com.aiwinn.faceattendance
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class AttApp extends Application {

    public static Context mContext;
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        sp = getSharedPreferences(AttConstants.PREFS, 0);
        AiwinnManager.getInstance().init(this);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }

    public static void initSDK() {
        AiwinnManager.getInstance().setDebug(AttConstants.DEBUG);
        FaceDetectManager.setDebug(AttConstants.DEBUG);
        Status status = FaceDetectManager.init(mContext);
        if (status == Status.Ok) {
            AttConstants.INIT_STATE = true;
        } else {
            AttConstants.INIT_STATE = false;
            AttConstants.INIT_STATE_ERROR = status;
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static void hideBottomUIMenu(Activity activity) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
