package com.aiwinn.gaa;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aiwinn.faceSDK.AgeInfo;
import com.aiwinn.faceSDK.FaceInfo;
import com.aiwinn.faceSDK.FaceInfoBean;
import com.aiwinn.faceSDK.GenderAndAgeInfo;
import com.aiwinn.faceSDK.GenderInfo;
import com.aiwinn.faceSDK.SDKManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aiwinn.gaa.AppConfig.CAMERA_ROT;
import static com.aiwinn.gaa.AppConfig.MinBox;
import static com.aiwinn.gaa.AppConfig.ScaleSizeLine;

public class AgeAndGenderActivity extends AppCompatActivity {

    private SurfaceView mCameraPreview;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private DrawImageView mImageView;
    private static final int IMAGE_FORMAT = ImageFormat.NV21;
    private ImageView mBtnCameraSwitch;
    private static final String TAG = "AgeAndGenderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_and_sex);
        initView();
    }

    private void initView() {
        mImageView = findViewById(R.id.imageView);
        mCameraPreview = findViewById(R.id.sv_recording);
        mBtnCameraSwitch = findViewById(R.id.btn_camera_switch);
        mSurfaceHolder = mCameraPreview.getHolder();
        mSurfaceHolder.addCallback(mCallback);
        mCameraPreview.setOnTouchListener(mOnTouchListener);
        mBtnCameraSwitch.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            releaseCamera();
            AppConfig.cameraID = AppConfig.cameraID == 0 ? 1 : 0;
            openCamera(AppConfig.cameraID);
        }
    };

    public boolean openCamera(int id) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(id);
                AppConfig.cameraID = id;
                if (mSurfaceHolder != null) {
                    setStartPreview(mCamera, mSurfaceHolder);
                }
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            focusOnTouch((int) event.getX(), (int) event.getY());
            return false;
        }
    };

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCameraPreview.setWillNotDraw(false);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setStartPreview(mCamera, mSurfaceHolder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCamera();
        }
    };


    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            SDKManager.getInstance().setScale(0.6f);
            SDKManager.getInstance().SetFaceMinSide(80);

            FaceInfoBean faceDetectBean = SDKManager.getInstance().
                    getFaceDetectBean(data, CAMERA_ROT, AppConfig.CAMERA_W, AppConfig.CAMERA_H);
            try {
                if (faceDetectBean != null) {
                    if (faceDetectBean.getFaceInfos().length > 0 && faceDetectBean.getFaceInfos()[0] != null) {
                        Log.i("lxltest", "FaceInfos:" + faceDetectBean.getFaceInfos()[0].toString());
                    } else {
                        Log.i("lxltest", "FaceInfos length = 0");
                    }
                } else {
                    Log.i("lxltest", "faceDetectBean is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mImageView.setFaceDetectBean(faceDetectBean);
            mImageView.invalidate();
            camera.addCallbackBuffer(data);



        }
    };

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 初始化相机
     *
     * @return camera
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
        }
        return camera;
    }

    /**
     * 检查是否具有相机功能
     *
     * @param context context
     * @return 是否具有相机功能
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * 在SurfaceView中预览相机内容
     *
     * @param camera camera
     * @param holder SurfaceHolder
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {

            setDefaultParameters();
            camera.setPreviewDisplay(holder);
            // camera.setDisplayOrientation(90);
            float rate = AppConfig.CAMERA_W > AppConfig.CAMERA_H ? ScaleSizeLine / (float) AppConfig.CAMERA_H : ScaleSizeLine / (float) AppConfig.CAMERA_W;
            SDKManager.getInstance().setScale(rate);
            SDKManager.getInstance().setMinBox(MinBox);
            SDKManager.getInstance().SetFaceMinSide(80);
            camera.setPreviewCallback(mPreviewCallback);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void checkCamera() {
        openCamera(AppConfig.CAMERA_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
//            mCamera = Camera.open(AppConfig.cameraID);
            checkCamera();
            if (mSurfaceHolder != null) {
                setStartPreview(mCamera, mSurfaceHolder);
            }
        }
    }

    private void focusOnTouch(int x, int y) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / mCameraPreview.getWidth() - 1000;
        int top = rect.top * 2000 / mCameraPreview.getHeight() - 1000;
        int right = rect.right * 2000 / mCameraPreview.getWidth() - 1000;
        int bottom = rect.bottom * 2000 / mCameraPreview.getHeight() - 1000;
        // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        focusOnRect(new Rect(left, top, right, bottom));
    }

    private void focusOnRect(Rect rect) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters(); // 先获取当前相机的参数配置对象
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            mCamera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            mCamera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
    }


    private void setDefaultParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        Camera.Size previewSize = getLargePreviewSize(mCamera);
        //AppConfig.CAMERA_H = previewSize.height;
        //AppConfig.CAMERA_W = previewSize.width;
        Log.d(TAG, "setDefaultParameters: " + AppConfig.CAMERA_H + "    " + AppConfig.CAMERA_W);
        parameters.setPreviewSize(AppConfig.CAMERA_W, AppConfig.CAMERA_H);
        mCamera.setParameters(parameters);
    }


    public static Camera.Size getLargePreviewSize(Camera camera) {
        if (camera != null) {
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for (int i = 1; i < sizes.size(); i++) {
                if (temp.width < sizes.get(i).width)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }
}
