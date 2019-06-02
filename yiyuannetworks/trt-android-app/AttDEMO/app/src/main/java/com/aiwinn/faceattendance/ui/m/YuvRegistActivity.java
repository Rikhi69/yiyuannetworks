package com.aiwinn.faceattendance.ui.m;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.base.widget.AttPopwindow;
import com.aiwinn.base.widget.CameraInterface;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.bean.RegisterFaceInfo;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.faceattendance.ui.p.YuvRegistPresenter;
import com.aiwinn.faceattendance.ui.p.YuvRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.v.YuvRegistView;
import com.aiwinn.faceattendance.utils.FaceUtils;
import com.aiwinn.faceattendance.widget.PierceMaskView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.FaceState;
import com.aiwinn.facedetectsdk.common.Status;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

/**
 * com.aiwinn.faceattendance.ui
 * SnapShot
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class YuvRegistActivity extends BaseActivity implements CameraInterface.CameraStateCallBack, YuvRegistView {

    private static final int MSG_DETECT_NO_FACE = 101;
    private static final int MSG_DETECT_NOT_CENTER = 102;
    private static final int MSG_DETECT_ERROR = 100;
    private static final int MSG_DETECT_FACE_INFO = 103;
    private static final int MSG_REGISTER_ERROR = 104;
    private static final int MSG_REGISTER_SUCCESS = 105;

    private SurfaceView mCsvRegister;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private PierceMaskView mPmvRegister;
    private TextView mTvNotify;
    private YuvRegistPresenter mPresenter;
    private Handler mHandler;
    private RelativeLayout mRlRoot;
    private AttPopwindow mPopupWindow;
    private EditText mPopWindowEditText;
    private ImageView mPopWindowImageView;
    private boolean mIsShowPopWindow;
    private TextView mCancelTextView;
    private TextView mSureTextView;
    private ImageView mBack;
    private boolean mLandScape;

    @Override
    public int getLayoutId() {
        return R.layout.activity_yuvregist;
    }

    @Override
    public void initViews() {
        mCsvRegister = findViewById(R.id.csv_register);
        mPmvRegister = findViewById(R.id.pmv_register);
        mTvNotify = findViewById(R.id.tv_notify);
        mRlRoot = findViewById(R.id.rl_register_root);
        mBack = findViewById(R.id.back);
    }

    @Override
    public void initData() {
        mIsShowPopWindow = false;
        mHandler = new RegisterHandler(this);
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
            mRadius = mPreviewWidth / 2 - 60;
        }
        mPmvRegister.setPiercePosition(mCenterX, mCenterY, mRadius);
        CameraInterface.getInstance().setCameraStateCallBack(this,mCsvRegister);
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID, AttConstants.PREVIEW_DEGREE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
        YuvRegistActivity.this.finish();
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
        YuvRegistActivity.this.finish();
    }

    @Override
    public void getCameraParameters() {
        mPreviewWidth = CameraInterface.getInstance().getPreviewWidth();
        mPreviewHeight = CameraInterface.getInstance().getPreviewHeight();
        FaceDetectManager.setDegree(AttConstants.CAMERA_DEGREE);
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
                CameraInterface.getInstance().doOpenCamera(id,degree);
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

        private WeakReference<YuvRegistActivity> mActivity;

        public RegisterHandler(YuvRegistActivity activity) {
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
            YuvRegistActivity activity = mActivity.get();
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
        YuvRegistActivity.this.finish();
    }

    private void registerResult(Status status) {

        switch (status) {
            case Ok:
                ToastUtils.showShort(getResources().getString(R.string.register_success));
                mPopupWindow.dissmiss();
                YuvRegistActivity.this.finish();
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
        mIsShowPopWindow = true;
        View contentView  = LayoutInflater.from(this).inflate(R.layout.register_popwindow, null);
        mPopupWindow = new AttPopwindow.PopupWindowBuilder(YuvRegistActivity.this)
                .setView(contentView )
                .enableOutsideTouchableDissmiss(false)
                .create()
                .showAtLocation(YuvRegistActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopWindowEditText = contentView.findViewById(R.id.et_register_name);
        mPopWindowImageView = contentView.findViewById(R.id.iv_register_pop);
        mCancelTextView = contentView.findViewById(R.id.btn_register_cancel);
        mSureTextView = contentView.findViewById(R.id.btn_register_sure);
        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dissmiss();
            }
        });
        mSureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mPopWindowEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showShort(getResources().getString(R.string.name_empty));
                } else {
                    RegisterBean registerBean = new RegisterBean(name);
                    mPresenter.saveRegisterInfo(faceInfo.getSrc(), faceInfo.getDetectBean(),registerBean);
                }
            }
        });
        Bitmap clipBitmap = FaceUtils.createBitmapfromDetectBean(faceInfo.getDetectBean(), faceInfo.getSrc());
        if (AttConstants.CAMERA_ID == 1 && AttConstants.CAMERA_DEGREE == 90) {
            Bitmap rotBitmap = ImageUtils.rotate(clipBitmap,180,0,0);
            clipBitmap = Bitmap.createBitmap(rotBitmap);
        }
        Glide.with(this).load(clipBitmap).into(mPopWindowImageView);
        mPopupWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogUtils.d(YuvRegistPresenterImpl.HEAD,"popwindow dismiss");
                mIsShowPopWindow = false;
                mPresenter.dealFaceInfoFinish();
            }
        });
    }
}
