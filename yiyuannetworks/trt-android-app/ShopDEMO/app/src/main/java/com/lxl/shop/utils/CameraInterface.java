package com.lxl.shop.utils;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.utils.CameraParametersUtils;

/**
 * 这个类是算法盒子的封装类，用来处理摄像头的一些功能
 * 由于私有封装，不能进行其他子自定义改造，此处进行重构，开发Camera对象
 */
public class CameraInterface implements Callback {
    public static String TAG = "CameraInterface";
    public static int PREVIEW_MODE = 0;
    public static int PREVIEW_EQUAL_SCREEN = 0;
    public static int PREVIEW_EQUAL_SCALE = 1;
    public static int PREVIEW_EQUAL_SIDE = 2;
    public static int PREVIEW_MAX = 3;
    public static int PREVIEW_CUSTOM = 4;
    private static CameraInterface mCameraInterface;
    private CameraInterface.CameraStateCallBack mCameraStateCallBack;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private Parameters mParams;
    private boolean isPreviewing = false;
    private boolean custom = false;
    private int mCustomWidth;
    private int mCustomHeight;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mDegree = 0;
    private int mCameraId;
    private boolean mLandscape;
    private int mStopTime = 300;
    boolean setParameterHasException = false;
    PreviewCallback mPreviewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] var1, Camera var2) {
            CameraInterface.this.mCameraStateCallBack.cameraHasPreview(var1, var2);
        }
    };

    void openCamera(SurfaceHolder var1) {
        try {
            this.mCamera = Camera.open(this.mCameraId);
            this.mCamera.setDisplayOrientation(this.mDegree);
            LogUtils.d(TAG, "Camera open ! id = " + this.mCameraId + " degree = " + this.mDegree);
            this.mCamera.setPreviewDisplay(var1);
            this.doStartPreview();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void surfaceCreated(SurfaceHolder var1) {
        LogUtils.d(TAG, "surface created");
        this.openCamera(var1);
    }

    public void surfaceChanged(SurfaceHolder var1, int var2, int var3, int var4) {
        LogUtils.d(TAG, "surface changed");
    }

    public void surfaceDestroyed(SurfaceHolder var1) {
        LogUtils.d(TAG, "surface destroyed");
    }

    public void setCameraStateCallBack(CameraInterface.CameraStateCallBack var1, SurfaceView var2) {
        this.mCameraStateCallBack = var1;
        this.mSurfaceView = var2;
    }

    private CameraInterface() {
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }

        return mCameraInterface;
    }

    public void doOpenCamera(int var1, int var2) {
        this.mCameraId = var1;
        this.mDegree = var2;
        this.mLandscape = ScreenUtils.isLandscape();
        this.mPreviewWidth = ScreenUtils.getScreenWidth();
        this.mPreviewHeight = ScreenUtils.getScreenHeight();
        LogUtils.d(TAG, "Open landscape = " + this.mLandscape + " ScreenWidth = " + this.mPreviewWidth + " ScreenHeight = " + this.mPreviewHeight + " Id = " + this.mCameraId + " Degree = " + this.mDegree);
        this.getSurfaceViewHolder();
    }

    public void doReOpenCamera(int var1, int var2) {
        this.mCameraId = var1;
        this.mDegree = var2;
        this.mLandscape = ScreenUtils.isLandscape();
        this.mPreviewWidth = ScreenUtils.getScreenWidth();
        this.mPreviewHeight = ScreenUtils.getScreenHeight();
        LogUtils.d(TAG, "ReOpen landscape = " + this.mLandscape + " ScreenWidth = " + this.mPreviewWidth + " ScreenHeight = " + this.mPreviewHeight + " Id = " + this.mCameraId + " Degree = " + this.mDegree);
        this.mCamera.setPreviewCallback((PreviewCallback)null);
        this.mCamera.stopPreview();
        this.mCamera.release();
        this.mCamera = null;
        this.isPreviewing = false;
        this.openCamera(this.getSurfaceViewHolder());
    }

    public void doStartPreview() {
        if (!this.isPreviewing) {
            if (this.mCamera != null) {
                this.setParameter();
                this.mCamera.setPreviewCallback(this.mPreviewCallback);
                this.mCameraStateCallBack.getCameraParameters();
                this.mCamera.startPreview();
                this.isPreviewing = true;
            }

        }
    }

    SurfaceHolder getSurfaceViewHolder() {
        SurfaceHolder var1 = this.mSurfaceView.getHolder();
        var1.addCallback(this);
        var1.setKeepScreenOn(true);
        var1.setFormat(-2);
        var1.setType(3);
        return var1;
    }

    public void doStopCamera() {
        if (null != this.mCamera) {
            this.mSurfaceView.getHolder().removeCallback(this);
            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.isPreviewing = false;

            try {
                Thread.sleep((long)this.mStopTime);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }
        }

    }

    void setParameter() {
        try {
            this.mParams = this.mCamera.getParameters();

            if (CameraParametersUtils.getBestPreviewSize(this.mParams, this.mLandscape, this.mPreviewWidth, this.mPreviewHeight)) {
                this.mPreviewWidth = CameraParametersUtils.bestPreviewWidth;
                this.mPreviewHeight = CameraParametersUtils.bestPreviewHeight;
                LogUtils.d(TAG, "Best Preview mPreviewWidth = " + this.mPreviewWidth + " ; mPreviewHeight = " + this.mPreviewHeight);
            } else {
                LogUtils.d(TAG, "Best Preview Fail");
                if (this.mLandscape) {
                    this.mPreviewWidth = ScreenUtils.getScreenWidth();
                    this.mPreviewHeight = ScreenUtils.getScreenHeight();
                } else {
                    this.mPreviewWidth = ScreenUtils.getScreenHeight();
                    this.mPreviewHeight = ScreenUtils.getScreenWidth();
                }

                PREVIEW_MODE = PREVIEW_EQUAL_SCREEN;
            }

            if (this.custom) {
                this.mPreviewWidth = this.mCustomWidth;
                this.mPreviewHeight = this.mCustomHeight;
                PREVIEW_MODE = PREVIEW_CUSTOM;
            }

            if (VERSION.SDK_INT >= 14 && !Build.MODEL.equals("GT-I9100")) {
                this.mParams.setRecordingHint(true);
            }

            LogUtils.d(TAG, "Final Preview mPreviewWidth = " + this.mPreviewWidth + " ; mPreviewHeight = " + this.mPreviewHeight + " custom = " + this.custom);
            this.mParams.setPreviewSize(this.mPreviewWidth, this.mPreviewHeight);
            if (!this.setParameterHasException) {
                this.mCamera.cancelAutoFocus();
                this.mParams.setFocusMode("continuous-picture");
            }

            this.mCamera.setParameters(this.mParams);
        } catch (Exception var2) {
            var2.printStackTrace();
            LogUtils.d(TAG, "Set Parameter Exception:" + var2.toString());
            this.setParameterHasException = true;
            this.setParameter();
        }

    }

    public int getPreviewWidth() {
        return this.mPreviewWidth;
    }

    public int getPreviewHeight() {
        return this.mPreviewHeight;
    }

    public int getDegree() {
        return this.mDegree;
    }

    public void setPreViewSize(int var1, int var2) {
        if (var1 != 0 && var2 != 0) {
            this.custom = true;
            this.mCustomWidth = var1;
            this.mCustomHeight = var2;
        } else {
            this.custom = false;
        }

    }

    public void setSleepTime(int var1) {
        if (var1 != 0) {
            this.mStopTime = var1;
        }

    }

    public interface CameraStateCallBack {
        void getCameraParameters();

        void cameraHasPreview(byte[] var1, Camera var2);
    }

    public Camera getmCamera() {
        return mCamera;
    }
}
