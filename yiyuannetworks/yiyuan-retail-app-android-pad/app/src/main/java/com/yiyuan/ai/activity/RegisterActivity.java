package com.yiyuan.ai.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.base.widget.AttPopwindow;
import com.aiwinn.base.widget.CameraInterfaceBak;
import com.aiwinn.base.widget.CameraSurfaceView;
import com.aiwinn.faceSDK.FaceInfoBean;
import com.aiwinn.faceSDK.SDKManager;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.FaceState;
import com.aiwinn.facedetectsdk.common.Status;
import com.bumptech.glide.Glide;
import com.yiyuan.ai.AIConstants;
import com.yiyuan.ai.FaceUtil;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.common.HttpUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.ai.view.FaceView;
import com.yiyuan.aiwinn.faceattendance.bean.RegisterFaceInfo;
import com.yiyuan.aiwinn.faceattendance.common.AttConstants;
import com.yiyuan.aiwinn.faceattendance.ui.p.YuvRegistPresenter;
import com.yiyuan.aiwinn.faceattendance.ui.p.YuvRegistPresenterImpl;
import com.yiyuan.aiwinn.faceattendance.ui.v.YuvRegistView;
import com.yiyuan.aiwinn.faceattendance.utils.FaceUtils;
import com.yiyuan.aiwinn.faceattendance.widget.PierceMaskView;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyu on 2019/4/11.
 */

public class RegisterActivity extends BaseActivity implements CameraInterfaceBak.CameraStateCallBack, YuvRegistView {
    private static final int MSG_DETECT_NO_FACE = 101;
    private static final int MSG_DETECT_NOT_CENTER = 102;
    private static final int MSG_DETECT_ERROR = 100;
    private static final int MSG_DETECT_FACE_INFO = 103;
    private static final int MSG_REGISTER_ERROR = 104;
    private static final int MSG_REGISTER_SUCCESS = 105;

    private CameraSurfaceView mCsvRegister;
    private TextView btnFinish;
    private TextView btnCancel;
    private ImageView iv;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private PierceMaskView mPmvRegister;
    private TextView mTvNotify;
    private YuvRegistPresenter mPresenter;
    private Handler mHandler;
    private boolean mIsShowPopWindow;
    private ImageView mBack;
    private RelativeLayout mRlRoot;
    private AttPopwindow mPopupWindow;
    private boolean mLandScape;
    private RegisterFaceInfo mFaceInfo;

    private Handler dialogHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                DialogUtil.finishMessage(mContext,(CustomerModel) msg.obj,1);
                DialogUtil.dissmisDialog();
            }else if(msg.what == 1){
                DialogUtil.dissmisDialog();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews() {
        mCsvRegister = findViewById(R.id.csv_register);
        mPmvRegister = findViewById(R.id.pmv_register);
        btnFinish = findViewById(R.id.btn_finish);
        btnCancel = findViewById(R.id.btn_cancel);
        mTvNotify = findViewById(R.id.tv_notify);
        mRlRoot = findViewById(R.id.rl_register_root);
        mBack = findViewById(R.id.back);
        iv = findViewById(R.id.show_iv);
        mCsvRegister.setLayoutParams( getLayoutPara(mCsvRegister.getLayoutParams(),ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()/3));
        mPmvRegister.setLayoutParams(getLayoutPara(mCsvRegister.getLayoutParams(),ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()/3));
        iv.setLayoutParams(getLayoutPara(iv.getLayoutParams(),ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()/3));
        btnFinish.setLayoutParams(getLayoutPara(btnFinish.getLayoutParams(),ScreenUtils.getScreenWidth()/4,0));
        btnCancel.setLayoutParams(getLayoutPara(btnFinish.getLayoutParams(),ScreenUtils.getScreenWidth()/4,0));
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
        mIsShowPopWindow = false;
        mHandler = new RegisterActivity.RegisterHandler(this);
        mPresenter = new YuvRegistPresenterImpl(this);
        mLandScape = ScreenUtils.isLandscape();
        int mPreviewWidth = ScreenUtils.getScreenWidth();
        int mPreviewHeight = ScreenUtils.getScreenHeight();
        int mCenterX;
        int mCenterY;
        int mRadius;
        if (mLandScape) {
            mCenterX = mPreviewWidth / 2;
            mCenterY = mPreviewHeight / 2;
            mRadius = mPreviewHeight / 2 - 60;
        }else {
            mCenterX = mPreviewWidth / 2;
            mCenterY = mPreviewHeight / 2;
            mRadius = mPreviewWidth / 3 - 60;
        }
        //mPmvRegister.setPiercePosition(mCenterX, mCenterY, mRadius);
        mPmvRegister.setRectPosition( 0,0,0,0);
        CameraInterfaceBak.getInstance().setCameraStateCallBack(this);
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.finishActivity(mContext);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsShowPopWindow = false;
                mPresenter.dealFaceInfoFinish();
                iv.setVisibility(View.GONE);
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFaceInfo == null){
                    ToastUtils.showLong(getResources().getString(R.string.register_dialog_photo_please));
                }else{
                    DialogUtil.showDialog(mContext,getResources().getString(R.string.register_saving));
                    //注册
                    FaceUtil.registerUserAI(mFaceInfo, new FaceView() {
                        @Override
                        public void success(final UserBean userBean) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomerModel customerIdDB = null;
                                    try {
                                        String imageUrl = HttpUtil.getInstance().toUploadFile(new File(userBean.localImagePath), new HashMap<String, String>());
                                        if(imageUrl == null){
                                            ToastUtils.showLong(getResources().getString(R.string.register_upload_img_fail));
                                            deleteRegisterByUserId(userBean.userId);
                                            dialogHandler.sendEmptyMessage(1);
                                            return;
                                        }else{
                                            JSONObject jsonObject = new JSONObject(imageUrl);
                                            imageUrl = (String)jsonObject.get("data");
                                        }
                                        CustomerModel customerModel = new CustomerModel();
                                        FaceInfoBean faceInfoBean = SDKManager.getInstance().getFaceDetectBeanByBitmap(HttpUtil.getInstance().uploadCustomer(imageUrl));
                                        customerModel.age = faceInfoBean.getAgeInfos() == null ? 0 : ((int) faceInfoBean.getAgeInfos()[0].age);
                                        customerModel.gender = faceInfoBean.getGenderInfos() == null ? "男" : faceInfoBean.getGenderInfos()[0].gender > 0.5F ? "男" : "女";
                                        customerModel.imgUrl = imageUrl;
                                        customerModel.name = userBean.name;
                                        CustomerModel customerModel1 = HttpUtil.getInstance().sendAddRegisterService(customerModel);
                                        if(StringUtils.isEmpty(customerModel1.customerId)){
                                            ToastUtils.showLong(getResources().getString(R.string.register_server_fail));
                                            deleteRegisterByUserId(userBean.userId);
                                            dialogHandler.sendEmptyMessage(1);
                                            return;
                                        }
                                        customerIdDB = customerModel1;
                                        FaceUtil.updateSpData(mContext,userBean.userId,customerModel1.name,customerModel1.customerId,customerModel1.gender);
                                    }catch (Exception e){
                                        ToastUtils.showLong(getResources().getString(R.string.register_server_fail_link));
                                        deleteRegisterByUserId(userBean.userId);
                                        dialogHandler.sendEmptyMessage(1);
                                        return;
                                    }
                                    ToastUtils.showLong(getResources().getString(R.string.register_server_success));
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    message.obj = customerIdDB;
                                    dialogHandler.sendMessage(message);
                                }
                            }).start();
                        }
                        @Override
                        public void error(String msg) {
                            ToastUtils.showLong(msg);
                            btnCancel.performClick();
                        }
                    });
                }
            }
        });
    }

    private void deleteRegisterByUserId(String userId) {
        List<UserBean> userBeanList = FaceDetectManager.queryByUserId(userId);
        if(userBeanList.size() >0 ) {
            FaceDetectManager.deleteByUserInfo(userBeanList);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID+ AIConstants.camerId, AttConstants.PREVIEW_DEGREE+AIConstants.camerDataDeg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterfaceBak.getInstance().doStopCamera();
        RegisterActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigLib.picScaleRate = 1;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ToastUtils.showShort(getResources().getString(R.string.reopen_activity));
        RegisterActivity.this.finish();
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
        mPresenter.registerFace(data, mPreviewWidth, mPreviewHeight);
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
    public void onError(Status status) {
        Message message = Message.obtain();
        message.what = MSG_DETECT_ERROR;
        message.obj = status;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void noFace() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DETECT_NO_FACE);
        }
        mPresenter.dealFaceInfoFinish();
    }

    @Override
    public void notCenter() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DETECT_NOT_CENTER);
        }
        mPresenter.dealFaceInfoFinish();
    }

    @Override
    public void faceInfo(Bitmap src, DetectBean maxFace) {
        Message message = Message.obtain();
        message.what = MSG_DETECT_FACE_INFO;
        message.obj = new RegisterFaceInfo(src, maxFace);
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onRegisterSuccess(UserBean userBean) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_REGISTER_SUCCESS);
        }
    }

    @Override
    public void onRegisterError(Status status) {
        Message message = Message.obtain();
        message.what = MSG_REGISTER_ERROR;
        message.obj = status;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    private static class RegisterHandler extends Handler {

        private WeakReference<RegisterActivity> mActivity;

        public RegisterHandler(RegisterActivity activity) {
            super();
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DETECT_NO_FACE:
                    mActivity.get().mTvNotify.setText(mActivity.get().getResources().getString(R.string.no_face));
                    break;
                case MSG_DETECT_NOT_CENTER:
                    mActivity.get().mTvNotify.setText(mActivity.get().getResources().getString(R.string.not_center));
                    break;
                case MSG_DETECT_ERROR:
                    Status status = (Status) msg.obj;
                    mActivity.get().mTvNotify.setText(status.toString());
                    break;
                case MSG_DETECT_FACE_INFO:
                    RegisterFaceInfo face = (RegisterFaceInfo) msg.obj;
                    dealFaceInfo(face);
                    break;
                case MSG_REGISTER_ERROR:
                    Status result = (Status) msg.obj;
                    mActivity.get().registerResult(result);
                    break;
                case MSG_REGISTER_SUCCESS:
                    mActivity.get().registerSuccess();
                    break;
                default:
                    break;
            }
        }

        private void dealFaceInfo(RegisterFaceInfo face) {
            RegisterActivity activity = mActivity.get();
            FaceState positionState = face.getDetectBean().faceState;
            switch (positionState) {
                case OK:
                    if (!mActivity.get().mIsShowPopWindow) {
                        mActivity.get().mTvNotify.setText(mActivity.get().getResources().getString(R.string.register));
                        mActivity.get().showPopWindow(face);
                    }
                    break;
                case BOW:
                    activity.updateFaceInfo(mActivity.get().getResources().getString(R.string.rise));
                    activity.mPresenter.dealFaceInfoFinish();
                    break;
                case RISE:
                    activity.updateFaceInfo(mActivity.get().getResources().getString(R.string.down));
                    activity.mPresenter.dealFaceInfoFinish();
                    break;
                case LEFT_DEVIATION:
                    activity.updateFaceInfo(mActivity.get().getResources().getString(R.string.keep_left));
                    activity.mPresenter.dealFaceInfoFinish();
                    break;
                case RIGHT_DEVIATION:
                    activity.updateFaceInfo(mActivity.get().getResources().getString(R.string.keep_right));
                    activity.mPresenter.dealFaceInfoFinish();
                    break;
            }
        }

    }

    private void updateFaceInfo(String msg) {
        mTvNotify.setText(msg);
        mPresenter.dealFaceInfoFinish();
    }

    private void registerSuccess() {
        ToastUtils.showShort(getResources().getString(R.string.register_success));
        mPopupWindow.dissmiss();
        RegisterActivity.this.finish();
    }

    private void registerResult(Status status) {

        switch (status) {
            case Ok:
                ToastUtils.showShort(getResources().getString(R.string.register_success));
                mPopupWindow.dissmiss();
                RegisterActivity.this.finish();
                break;
            default:
                notifyResult(status);
                break;
        }
    }

    private void notifyResult(Status status) {
        ToastUtils.showShort(status.toString());
        mPopupWindow.dissmiss();
    }

    private void showPopWindow(final RegisterFaceInfo faceInfo) {
        iv.setVisibility(View.VISIBLE);
        Bitmap clipBitmap = FaceUtils.createBitmapfromDetectBean(faceInfo.getDetectBean(), faceInfo.getSrc());
        iv.setImageBitmap(clipBitmap);
        mFaceInfo = faceInfo;
//        mIsShowPopWindow = true;
//        View contentView  = LayoutInflater.from(this).inflate(R.layout.register_popwindow, null);
//        mPopupWindow = new AttPopwindow.PopupWindowBuilder(RegisterActivity.this)
//                .setView(contentView )
//                .enableOutsideTouchableDissmiss(false)
//                .create()
//                .showAtLocation(RegisterActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//        mPopWindowEditText = contentView.findViewById(R.id.et_register_name);
//        mPopWindowImageView = contentView.findViewById(R.id.iv_register_pop);
//        mCancelTextView = contentView.findViewById(R.id.btn_register_cancel);
//        mSureTextView = contentView.findViewById(R.id.btn_register_sure);
//        mCancelTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPopupWindow.dissmiss();
//            }
//        });
//        mSureTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = mPopWindowEditText.getText().toString().trim();
//                if (TextUtils.isEmpty(name)) {
//                    ToastUtils.showShort(getResources().getString(R.string.name_empty));
//                } else {
//                    RegisterBean registerBean = new RegisterBean(name);
//                    mPresenter.saveRegisterInfo(faceInfo.getSrc(), faceInfo.getDetectBean(),registerBean);
//                }
//            }
//        });
//        Bitmap clipBitmap = FaceUtils.createBitmapfromDetectBean(faceInfo.getDetectBean(), faceInfo.getSrc());
//        if (AttConstants.CAMERA_ID == 1 && AttConstants.CAMERA_DEGREE == 90) {
//            Bitmap rotBitmap = ImageUtils.rotate(clipBitmap,180,0,0);
//            clipBitmap = Bitmap.createBitmap(rotBitmap);
//        }
//        Glide.with(this).load(clipBitmap).into(mPopWindowImageView);
//        mPopupWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                LogUtils.d(YuvRegistPresenterImpl.HEAD,"popwindow dismiss");
//                mIsShowPopWindow = false;
//                mPresenter.dealFaceInfoFinish();
//            }
//        });
    }
}