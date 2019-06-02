package com.lxl.shop;

import android.Manifest;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.aiwinn.base.util.FileUtils;
import com.aiwinn.base.util.PermissionUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;
import com.lxl.mobile.page.PageInitActivity;
import com.lxl.shop.common.AttConstants;
import com.lxl.shop.utils.CrashHandler;

import java.util.List;

/**
 * Created by yanglei on 2018/11/26.
 */

public abstract class InitActivity extends PageInitActivity implements PermissionUtils.FullCallback {

    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    protected boolean mIsGranted = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsGranted = true;
        if (Build.VERSION.SDK_INT <= 20) {
            mIsGranted = true;
        } else {
            for (int i = 0; i < permissions.length; i++) {
                if (!PermissionUtils.isGranted(permissions[i])) {
                    mIsGranted = false;
                    break;
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mIsGranted) {
            if (!AttConstants.INIT_STATE) {
                FileUtils.createOrExistsDir(AttConstants.PATH_AIWINN);
                FileUtils.createOrExistsDir(AttConstants.PATH_ATTENDANCE);
                FileUtils.createOrExistsDir(AttConstants.PATH_BULK_REGISTRATION);
                FileUtils.createOrExistsDir(AttConstants.PATH_CARD);
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                int cameraCount = Camera.getNumberOfCameras();
                for (int i = 0; i < cameraCount; i++) {
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        AttConstants.hasBackCamera = true;
                    } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        AttConstants.hasFrontCamera = true;
                    }
                }
                AttConstants.cameraCount = cameraCount;
                if (AttConstants.hasBackCamera) {
                    AttConstants.CAMERA_ID = AttConstants.CAMERA_BACK_ID;
                    AttConstants.CAMERA_DEGREE = AttConstants.CAMERA_BACK_DEGREE;
                }
                if (!AttConstants.hasBackCamera && AttConstants.hasFrontCamera) {
                    AttConstants.CAMERA_ID = AttConstants.CAMERA_FRONT_ID;
                    AttConstants.CAMERA_DEGREE = AttConstants.CAMERA_FRONT_DEGREE;
                }
                AttConstants.CAMERA_PREVIEW_HEIGHT = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_PREVIEW_SIZE, AttConstants.CAMERA_PREVIEW_HEIGHT);
                ConfigLib.picScaleSize = AttApp.sp.getFloat(AttConstants.PREFS_TRACKER_SIZE, ConfigLib.picScaleSize);
                ConfigLib.picScaleRate = AttApp.sp.getFloat(AttConstants.PREFS_DETECT_RATE, ConfigLib.picScaleRate);
                ConfigLib.Nv21ToBitmapScale = AttApp.sp.getInt(AttConstants.PREFS_FEATURE_SIZE, ConfigLib.Nv21ToBitmapScale);
                FaceDetectManager.setFaceMinRect(AttApp.sp.getInt(AttConstants.PREFS_DETECT_SIZE, FaceDetectManager.getFaceMinRect()));
                AttConstants.CAMERA_ID = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_ID, AttConstants.CAMERA_ID);
//                AttConstants.CAMERA_DEGREE = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_DEGREE, AttConstants.CAMERA_DEGREE);
//                AttConstants.PREVIEW_DEGREE = AttApp.sp.getInt(AttConstants.PREFS_PREVIEW_DEGREE, AttConstants.PREVIEW_DEGREE);
                AttConstants.LEFT_RIGHT = AttApp.sp.getBoolean(AttConstants.PREFS_LR, AttConstants.LEFT_RIGHT);
                AttConstants.TOP_BOTTOM = AttApp.sp.getBoolean(AttConstants.PREFS_TB, AttConstants.TOP_BOTTOM);
                ConfigLib.detectWithRecognition = AttApp.sp.getBoolean(AttConstants.PREFS_RECOGNITION, ConfigLib.detectWithRecognition);
                ConfigLib.detectWithLiveness = AttApp.sp.getBoolean(AttConstants.PREFS_LIVENESS, ConfigLib.detectWithLiveness);
                ConfigLib.featureThreshold = AttApp.sp.getFloat(AttConstants.PREFS_UNLOCK, ConfigLib.featureThreshold);
                ConfigLib.livenessThreshold = AttApp.sp.getFloat(AttConstants.PREFS_LIVENESST, ConfigLib.livenessThreshold);
                ConfigLib.registerPicRect = AttApp.sp.getInt(AttConstants.PREFS_FACEMINIMA, ConfigLib.registerPicRect);
                ConfigLib.maxRegisterBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MAXREGISTERBRIGHTNESS, ConfigLib.maxRegisterBrightness);
                ConfigLib.minRegisterBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MINREGISTERBRIGHTNESS, ConfigLib.minRegisterBrightness);
                ConfigLib.blurRegisterThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURREGISTERTHRESHOLD, ConfigLib.blurRegisterThreshold);
                ConfigLib.maxRecognizeBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MAXRECOGNIZEBRIGHTNESS, ConfigLib.maxRecognizeBrightness);
                ConfigLib.minRecognizeBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MINRECOGNIZEBRIGHTNESS, ConfigLib.minRecognizeBrightness);
                ConfigLib.blurRecognizeThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURECOGNIZETHRESHOLD, ConfigLib.blurRecognizeThreshold);
                ConfigLib.blurRecognizeNewThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURECOGNIZENEWTHRESHOLD, ConfigLib.blurRecognizeNewThreshold);
                ConfigLib.Register_Check_Blur = false;
                ConfigLib.Register_Check_FaceState = true;
                Constants.DEBUG_SAVE_BLUR = AttApp.sp.getBoolean(AttConstants.PREFS_SAVEBLURDATA, Constants.DEBUG_SAVE_BLUR);
                Constants.DEBUG_SAVE_NOFACE = AttApp.sp.getBoolean(AttConstants.PREFS_SAVENOFACEDATA, Constants.DEBUG_SAVE_NOFACE);
                Constants.DEBUG_SAVE_SIMILARITY_SMALL = AttApp.sp.getBoolean(AttConstants.PREFS_SAVESSDATA, Constants.DEBUG_SAVE_SIMILARITY_SMALL);
                AttConstants.DEBUG = AttApp.sp.getBoolean(AttConstants.PREFS_DEBUG, AttConstants.DEBUG);
//                Constants.TRACKER_MODE = AttApp.sp.getInt(AttConstants.PREFS_TRACKER_MODE, Constants.TRACKER_MODE);
                FaceDetectManager.setDetectFaceMode(AttApp.sp.getInt(AttConstants.PREFS_DETECT_MODE, FaceDetectManager.getDetectFaceMode()));
                AttApp.initSDK();
                CrashHandler.getInstance().init(this);//这个必须在AttApp.initSDK()初始化后
            }
        } else {
            PermissionUtils.permission(permissions).callback(this).request();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGranted(List<String> list) {
        mIsGranted = true;
    }

    @Override
    public void onDenied(List<String> list, List<String> list1) {
        mIsGranted = false;
    }
}
