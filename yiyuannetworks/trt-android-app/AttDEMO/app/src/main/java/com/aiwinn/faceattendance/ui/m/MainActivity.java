package com.aiwinn.faceattendance.ui.m;

import android.Manifest;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.util.AppUtils;
import com.aiwinn.base.util.FileUtils;
import com.aiwinn.base.util.PermissionUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.faceattendance.AttApp;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.adapter.ActivityAdapter;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.m
 * SDK_ATT
 * 2018/08/29
 * Created by LeoLiu on User
 */

public class MainActivity extends BaseActivity implements View.OnClickListener ,PermissionUtils.FullCallback{

    TextView mVersion;
    TextView mName;

    RecyclerView mRecyclerView;
    ArrayList<String> mStringArrayList;

    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean mIsGranted;
    private ActivityAdapter mActivityAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initViews() {
        mVersion = findViewById(R.id.version);
        mName = findViewById(R.id.name);
        mRecyclerView = findViewById(R.id.activity);
    }

    @Override
    public void initData() {
        mVersion.setText("APP V_"+getVersionName());
        mName.setText(getResources().getString(R.string.app_name));
        mIsGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (!PermissionUtils.isGranted(permissions[i])) {
                mIsGranted = false;
                break;
            }
        }
        GridLayoutManager lm = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(lm);
        mStringArrayList = new ArrayList<>();
        mStringArrayList.clear();
        mActivityAdapter = new ActivityAdapter(mStringArrayList);
        mRecyclerView.setAdapter(mActivityAdapter);
        prepareActivityString();
    }

    private void prepareActivityString() {
        mStringArrayList.add(getResources().getString(R.string.yuvregist));//相机注册
        mStringArrayList.add(getResources().getString(R.string.bmpregist));//图片注册
        mStringArrayList.add(getResources().getString(R.string.detect));//探测
        mStringArrayList.add(getResources().getString(R.string.config));//配置
        mStringArrayList.add(getResources().getString(R.string.list));//注册列表
        mStringArrayList.add(getResources().getString(R.string.bulkregist));//批量注册
        mStringArrayList.add(getResources().getString(R.string.authorization));//授权
        mActivityAdapter.replaceData(mStringArrayList);
    }

    @Override
    public void initListeners() {
        mActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){

                    case 0:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, YuvRegistActivity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 1:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, BmpRegistActvity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 2:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, DetectActivity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 3:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, ConfigActivity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 4:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, RegisterListActivity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 5:
                        if (checkInitState()) {
                            mIntent.setClass(MainActivity.this, BulkRegistActivity.class);
                            startActivity(mIntent);
                        }
                        break;

                    case 6:
                        if (Constants.SDK_AUTHORIZATION_VERSION) {
                            mIntent.setClass(MainActivity.this, AuthorizationActivity.class);
                            startActivity(mIntent);
                        }else {
                            ToastUtils.showLong(getResources().getString(R.string.authorization_not));
                        }
                        break;
                }
            }
        });
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                Toast.makeText(this, getResources().getString(R.string.press_exit), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                FaceDetectManager.release();
                AppUtils.exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    }else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
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
                AttConstants.CAMERA_PREVIEW_HEIGHT = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_PREVIEW_SIZE,AttConstants.CAMERA_PREVIEW_HEIGHT);
                ConfigLib.picScaleSize = AttApp.sp.getFloat(AttConstants.PREFS_TRACKER_SIZE,ConfigLib.picScaleSize);
                ConfigLib.picScaleRate = AttApp.sp.getFloat(AttConstants.PREFS_DETECT_RATE,ConfigLib.picScaleRate);
                ConfigLib.Nv21ToBitmapScale = AttApp.sp.getInt(AttConstants.PREFS_FEATURE_SIZE,ConfigLib.Nv21ToBitmapScale);
                FaceDetectManager.setFaceMinRect(AttApp.sp.getInt(AttConstants.PREFS_DETECT_SIZE,FaceDetectManager.getFaceMinRect()));
                AttConstants.CAMERA_ID = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_ID,AttConstants.CAMERA_ID);
                AttConstants.CAMERA_DEGREE = AttApp.sp.getInt(AttConstants.PREFS_CAMERA_DEGREE,AttConstants.CAMERA_DEGREE);
                AttConstants.PREVIEW_DEGREE = AttApp.sp.getInt(AttConstants.PREFS_PREVIEW_DEGREE,AttConstants.PREVIEW_DEGREE);
                AttConstants.LEFT_RIGHT = AttApp.sp.getBoolean(AttConstants.PREFS_LR,AttConstants.LEFT_RIGHT);
                AttConstants.TOP_BOTTOM = AttApp.sp.getBoolean(AttConstants.PREFS_TB,AttConstants.TOP_BOTTOM);
                ConfigLib.detectWithRecognition = AttApp.sp.getBoolean(AttConstants.PREFS_RECOGNITION,ConfigLib.detectWithRecognition);
                ConfigLib.detectWithLiveness = AttApp.sp.getBoolean(AttConstants.PREFS_LIVENESS,ConfigLib.detectWithLiveness);
                ConfigLib.featureThreshold = AttApp.sp.getFloat(AttConstants.PREFS_UNLOCK,ConfigLib.featureThreshold);
                ConfigLib.livenessThreshold = AttApp.sp.getFloat(AttConstants.PREFS_LIVENESST,ConfigLib.livenessThreshold);
                ConfigLib.registerPicRect = AttApp.sp.getInt(AttConstants.PREFS_FACEMINIMA,ConfigLib.registerPicRect);
                ConfigLib.maxRegisterBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MAXREGISTERBRIGHTNESS,ConfigLib.maxRegisterBrightness);
                ConfigLib.minRegisterBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MINREGISTERBRIGHTNESS,ConfigLib.minRegisterBrightness);
                ConfigLib.blurRegisterThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURREGISTERTHRESHOLD,ConfigLib.blurRegisterThreshold);
                ConfigLib.maxRecognizeBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MAXRECOGNIZEBRIGHTNESS,ConfigLib.maxRecognizeBrightness);
                ConfigLib.minRecognizeBrightness = AttApp.sp.getFloat(AttConstants.PREFS_MINRECOGNIZEBRIGHTNESS,ConfigLib.minRecognizeBrightness);
                ConfigLib.blurRecognizeThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURECOGNIZETHRESHOLD,ConfigLib.blurRecognizeThreshold);
                ConfigLib.blurRecognizeNewThreshold = AttApp.sp.getFloat(AttConstants.PREFS_BLURECOGNIZENEWTHRESHOLD,ConfigLib.blurRecognizeNewThreshold);
                Constants.DEBUG_SAVE_BLUR = AttApp.sp.getBoolean(AttConstants.PREFS_SAVEBLURDATA,Constants.DEBUG_SAVE_BLUR);
                Constants.DEBUG_SAVE_NOFACE = AttApp.sp.getBoolean(AttConstants.PREFS_SAVENOFACEDATA,Constants.DEBUG_SAVE_NOFACE);
                Constants.DEBUG_SAVE_SIMILARITY_SMALL = AttApp.sp.getBoolean(AttConstants.PREFS_SAVESSDATA,Constants.DEBUG_SAVE_SIMILARITY_SMALL);
                AttConstants.DEBUG = AttApp.sp.getBoolean(AttConstants.PREFS_DEBUG,AttConstants.DEBUG);
                Constants.TRACKER_MODE = AttApp.sp.getInt(AttConstants.PREFS_TRACKER_MODE,Constants.TRACKER_MODE);
                FaceDetectManager.setDetectFaceMode(AttApp.sp.getInt(AttConstants.PREFS_DETECT_MODE,FaceDetectManager.getDetectFaceMode()));
                AttApp.initSDK();
            }
        }else{
            PermissionUtils.permission(permissions).callback(this).request();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            default:break;
        }
    }

    boolean checkInitState(){
        if (!AttConstants.INIT_STATE) {
            ToastUtils.showLong(getResources().getString(R.string.init_fail)+" : "+ AttConstants.INIT_STATE_ERROR);
            return false;
        }else {
            return true;
        }
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
