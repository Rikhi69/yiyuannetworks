package com.yiyuan.ai.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.base.widget.CameraInterfaceBak;
import com.aiwinn.base.widget.CameraSurfaceView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Status;
import com.yiyuan.ai.AIConstants;
import com.yiyuan.ai.R;
import com.yiyuan.ai.SocketUserUtil;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.common.HttpUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.aiwinn.faceattendance.common.AttConstants;
import com.yiyuan.aiwinn.faceattendance.ui.p.DetectPresenter;
import com.yiyuan.aiwinn.faceattendance.ui.p.DetectPresenterImpl;
import com.yiyuan.aiwinn.faceattendance.ui.p.YuvRegistPresenter;
import com.yiyuan.aiwinn.faceattendance.ui.p.YuvRegistPresenterImpl;
import com.yiyuan.aiwinn.faceattendance.ui.v.DetectView;
import com.yiyuan.aiwinn.faceattendance.ui.v.YuvRegistView;
import com.yiyuan.aiwinn.faceattendance.widget.PierceMaskView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyu on 2019/4/11.
 */

public class QueryMessageDialogActivity extends Activity implements CameraInterfaceBak.CameraStateCallBack, DetectView {

    private CameraSurfaceView mCsvRegister;
    private PierceMaskView mPmvRegister;

    private ImageView iv_close;
    private DetectPresenter mPresenter;

    private Context context;
    private int mPreviewWidth;
    private int mPreviewHeight;

    private boolean isFindUser;

    private int delayNewCustomerCount = 10;//连续三次识别成新用户之后会提示
    private int nowNewCustomerCount = 0;//当前识别次数
    //有一个bug是人脸识别对象会缓存上次请求的数据
    private int delayOldCustomerCount = 2;//连续三次识别成新用户之后会提示
    private int nowOldCustomerCount = 0;//当前识别次数
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){//显示用户信息
                DialogUtil.dissmisDialog();
                CustomerModel customerModel = (CustomerModel) msg.obj;
//                DialogUtil.finishMessage(context,customerModel,1);
                Intent i = new Intent(QueryMessageDialogActivity.this,CustomerInfoActivity.class);
                i.putExtra("customerModel",customerModel);
                startActivity(i);
                finish();
            }else if(msg.what == -1){
                DialogUtil.dissmisDialog();
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ai_activity_dialog);
        context = this;
        isFindUser = false;
        initView();
        initData();
    }

    private void initData() {
        mPresenter = new DetectPresenterImpl(this);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        mCsvRegister = findViewById(R.id.csv_register);
        mPmvRegister = findViewById(R.id.pmv_register);
        iv_close = findViewById(R.id.iv_close);
        mCsvRegister.setLayoutParams( getLayoutPara(mCsvRegister.getLayoutParams(), ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()/3));
        mPmvRegister.setLayoutParams(getLayoutPara(mCsvRegister.getLayoutParams(),ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()/3));
        mPmvRegister.setRectPosition( 0,0,0,0);
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
        mPreviewWidth = CameraInterfaceBak.getInstance().getPreviewWidth();
        mPreviewHeight = CameraInterfaceBak.getInstance().getPreviewHeight();
        FaceDetectManager.setDegree(AttConstants.CAMERA_DEGREE+AIConstants.dataDeg);
        ConfigLib.picScaleRate = mPreviewWidth > mPreviewHeight ? (float) ConfigLib.Nv21ToBitmapScale / (float) mPreviewWidth : (float) ConfigLib.Nv21ToBitmapScale / (float) mPreviewHeight;
    }

    @Override
    public void cameraHasPreview(byte[] data, Camera camera) {
        dissmisDialog();
        mPresenter.detectFaceData(data, mPreviewWidth, mPreviewHeight);
    }

    public void dissmisDialog() {
        if(this.dialog != null) {
            this.dialog.dismiss();
        }
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
    protected void onResume() {
        super.onResume();
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID+AIConstants.camerId, AttConstants.PREVIEW_DEGREE+ AIConstants.camerDataDeg);
    }
    private AlertDialog dialog;
    public void showDialog(String var1) {
        if(this.dialog == null) {
            this.dialog = new ProgressDialog(this);
        }

        this.dialog.setMessage(var1);
        if(!this.dialog.isShowing()) {
            this.dialog.show();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        CameraInterfaceBak.getInstance().doStopCamera();
        QueryMessageDialogActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigLib.picScaleRate = 1;
    }

    @Override
    public void recognizeFace(UserBean userBean) {
        LogUtils.d("recognizeFace");
    }

    @Override
    public void recognizeFaceNotMatch(UserBean userBean) {
        LogUtils.d("recognizeFaceNotMatch");
    }

    @Override
    public void detectNoFace() {
        LogUtils.d("detectNoFace");
    }

    @Override
    public void detectFail(Status status) {
        LogUtils.d("detectFail");
    }

    @Override
    public void detectFace(final List<FaceBean> faceBeans) {
        LogUtils.d("detectFace");
        //扫描到的第一个用户进行处理
        if(faceBeans.size() >0 && !isFindUser){
            if(!StringUtils.isEmpty(faceBeans.get(0).mUserBean.userId)) {
                nowOldCustomerCount++;
                if(nowOldCustomerCount<delayOldCustomerCount)return;
                nowOldCustomerCount = 0;
                isFindUser = true;
                nowNewCustomerCount = 0;
                DialogUtil.showDialog(context, getString(R.string.query_dialog_detect_success));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String customerId = SocketUserUtil.getCustomIdByUserId(context, faceBeans.get(0).mUserBean.userId);
                            CustomerModel customerModelDB = HttpUtil.getInstance().getCustomerById(customerId);
                            if (!StringUtils.isEmpty(customerModelDB.customerId)) {
                                Message message = Message.obtain();
                                message.what = 0;
                                message.obj = customerModelDB;
                                if (mHandler != null) {
                                    mHandler.sendMessage(message);
                                }
                            } else {
                                mHandler.sendEmptyMessage(-1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(-1);
                        }
                    }
                }).start();
            }else{
                nowNewCustomerCount++;
                if(nowNewCustomerCount>=delayNewCustomerCount) {
                    isFindUser = true;
                    nowNewCustomerCount = 0;
                    DialogUtil.registerDialog(context, new DialogUtil.FeedBack() {
                        @Override
                        public void sure() {
                            isFindUser = false;
                            Intent mIntent = new Intent();
                            mIntent.setClass(context, RegisterActivity.class);
                            context.startActivity(mIntent);
                            ((Activity) context).finish();
                        }

                        @Override
                        public void cancel() {
                            isFindUser = false;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void debug(FaceBean faceBean) {
        LogUtils.d("debug");
    }
}
