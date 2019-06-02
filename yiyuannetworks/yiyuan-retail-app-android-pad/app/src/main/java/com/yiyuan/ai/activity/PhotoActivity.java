package com.yiyuan.ai.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.widget.CameraInterfaceBak;
import com.aiwinn.base.widget.CameraSurfaceView;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.yiyuan.ai.AIConstants;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.common.ImageLoader;
import com.yiyuan.aiwinn.faceattendance.common.AttConstants;

import java.io.ByteArrayOutputStream;

/**
 * Created by wangyu on 2019/4/15.
 */

public class PhotoActivity extends BaseActivity implements CameraInterfaceBak.CameraStateCallBack{
    private CameraSurfaceView mCsvRegister;
    private RelativeLayout rlMain;
    private ImageView shouye;
    private ImageView photo;
    private byte[] dataImage;
    public static Bitmap dataImageBitmap;

    private int cameraWidth = 0;
    private int cameraHeight = 0;

    @Override
    public int getLayoutId() {
        return R.layout.ai_activity_photo;
    }

    @Override
    public void initViews() {
        //开启摄像头
        mCsvRegister = findViewById(R.id.csv_register);
        shouye = findViewById(R.id.shouye);
        photo = findViewById(R.id.photo);
        rlMain = findViewById(R.id.rl_main);
        rlMain.setLayoutParams( getLayoutPara(rlMain.getLayoutParams(), 0,ScreenUtils.getScreenHeight()*4/5));
        CameraInterfaceBak.getInstance().setCameraStateCallBack(this);
    }

    private ViewGroup.LayoutParams getLayoutPara(ViewGroup.LayoutParams layoutParams, int w, int h) {
        if(w>0)
            layoutParams.width = (int) w;
        if(h>0)
            layoutParams.height = (int) h;
        return layoutParams;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListeners() {
        shouye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.finishActivity(mContext);
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataImageBitmap = getBitmapByPhotoBytes();
                Intent i = new Intent(PhotoActivity.this,PhotoDialogActivity.class);
                startActivity(i);
            }

        });
    }

    private Bitmap getBitmapByPhotoBytes() {
        byte[] dataTemp = dataImage.clone();
        //处理data
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                dataTemp,
                ImageFormat.NV21,
                cameraWidth,
                cameraHeight,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, cameraWidth, cameraHeight), 100, baos);// 80--JPG图片的质量[0-100],100最高
        dataTemp = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap =  BitmapFactory.decodeByteArray(dataTemp, 0, dataTemp.length, options);
        return ImageLoader.rotateBitmapByDegree(bitmap,270);
    }
    @Override
    public void cameraHasOpened() {
        while (true) {
            if (!mCsvRegister.hasCreated) {
                LogUtils.d("wait CameraSurfaceView created !");
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        CameraInterfaceBak.getInstance().doStartPreview(mCsvRegister.getSurfaceHolder());
    }

    @Override
    public void cameraHasParameters() {

    }

    @Override
    public void cameraHasPreview(byte[] bytes, Camera camera) {
        dissmisDialog();
        dataImage = bytes;
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        cameraWidth = previewSize.width;
        cameraHeight = previewSize.height;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID+ AIConstants.camerId, AttConstants.PREVIEW_DEGREE+AIConstants.camerDataDeg);
    }

    private void openCamera(final int id, final int degree) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CameraInterfaceBak.getInstance().doOpenCamera(id,degree);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterfaceBak.getInstance().doStopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigLib.picScaleRate = 1;
    }
}
