package com.aiwinn.faceattendance.ui.m;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.FileUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.TimeUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.base.widget.CameraInterface;
import com.aiwinn.deblocks.utils.FeatureUtils;
import com.aiwinn.faceattendance.AttApp;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.adapter.DetectAdapter;
import com.aiwinn.faceattendance.bean.DetectFaceBean;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.faceattendance.ui.p.BulkRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.p.DetectPresenter;
import com.aiwinn.faceattendance.ui.p.DetectPresenterImpl;
import com.aiwinn.faceattendance.ui.v.DetectView;
import com.aiwinn.faceattendance.widget.MaskView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.ExtractFeatureListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * com.aiwinn.faceattendance.ui.m
 * SnapShot
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class DetectActivity extends BaseActivity implements CameraInterface.CameraStateCallBack, DetectView {

    private RelativeLayout mOther;
    private RecyclerView mRecyclerView;
    private MaskView mMaskView;
    private SurfaceView mSurfaceView;
    private TextView mTextView;
    private ImageView mBack;
    private ImageView mChangeCamera;
    private DetectPresenter mPresenter;
    private DetectAdapter mDetectAdapter;
    private ArrayList<DetectFaceBean> detectList;
    private DetectHandler mHandler;
    private int mPreviewWidth;
    private int mPreviewHeight;

    private final static int MSG_DETECT_NO_FACE = 0;
    private final static int MSG_DETECT_FAIL = 1;
    private final static int MSG_DETECT_DATA = 2;
    private final static int MSG_FACE = 3;

    private TextView mTvRegister;
    private TextView mTvCompare;
    private TextView mTvCompareCancel;
    private TextView mTvCompareMsg;
    private ImageView mImCompareWait;
    private ImageView mImCompareSucc;

    boolean isAsyncDoing = false;

    void doAsync() {
        isAsyncDoing = true;
    }

    void doAsyncDone() {
        isAsyncDoing = false;
    }

    ArrayList<Float> features = new ArrayList<>();
    private boolean mLandScape;

    private static class DetectHandler extends Handler {

        final WeakReference<DetectActivity> mActivity;

        public DetectHandler(DetectActivity detectActivity) {
            mActivity = new WeakReference<>(detectActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_DETECT_DATA:
                    mActivity.get().updateMessgae((String) msg.obj);
                    break;

                case MSG_DETECT_FAIL:
                    mActivity.get().updateMessgae(((Status) msg.obj).toString());
                    mActivity.get().mMaskView.clearRect();
                    break;

                case MSG_DETECT_NO_FACE:
                    mActivity.get().updateMessgae(mActivity.get().getResources().getString(R.string.no_face));
                    mActivity.get().mMaskView.clearRect();
                    break;

                case MSG_FACE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Glide.with(mActivity.get()).load(bitmap).into(mActivity.get().mImCompareSucc);
                    break;

            }
        }
    }

    void updateMessgae(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("w = " + mPreviewWidth + " h = " + mPreviewHeight);
        stringBuilder.append("\n");
        stringBuilder.append(s);
        mTextView.setText(stringBuilder.toString());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_detect;
    }

    @Override
    public void initViews() {
        AttApp.hideBottomUIMenu(this);
        mOther = findViewById(R.id.other);
        mChangeCamera = findViewById(R.id.exchangeCamera);
        mTvRegister = findViewById(R.id.tvregister);
        mTvCompare = findViewById(R.id.tvcompare);
        mTvCompareCancel = findViewById(R.id.tvcomparecancel);
        mTvCompareMsg = findViewById(R.id.tvcomparemsg);
        mImCompareWait = findViewById(R.id.imcomparewait);
        mImCompareSucc = findViewById(R.id.imcomparesucc);
        mSurfaceView = findViewById(R.id.sv);
        mRecyclerView = findViewById(R.id.rv);
        mBack = findViewById(R.id.back);
        mTextView = findViewById(R.id.message);
        mMaskView = findViewById(R.id.kcfmv);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void initData() {
        mHandler = new DetectHandler(this);
        mPresenter = new DetectPresenterImpl(this);
        CameraInterface.getInstance().setCameraStateCallBack(this, mSurfaceView);
        LinearLayoutManager detectRvManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(detectRvManager);
        detectList = new ArrayList<>();
        detectList.clear();
        mDetectAdapter = new DetectAdapter(detectList);
        mRecyclerView.setAdapter(mDetectAdapter);
        mLandScape = ScreenUtils.isLandscape();
        if (!ScreenUtils.isLandscape()) {
            mOther.setVisibility(View.GONE);
        }
        if (AttConstants.cameraCount == 1) {
            mChangeCamera.setVisibility(View.GONE);
        }
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID, AttConstants.PREVIEW_DEGREE);
//        mSurfaceView.setVisibility(View.GONE);
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetectActivity.this.finish();
            }
        });
        mTvCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAsyncDoing) {
                    return;
                }
                deal(true);
            }
        });
        mTvCompareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCompare();
            }
        });
        mTvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAsyncDoing) {
                    return;
                }
                deal(false);
            }
        });
        mChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeCamera();
            }
        });
    }

    private void deal(final boolean isCompare) {
        doAsync();
        List<File> files = FileUtils.listFilesInDir(AttConstants.PATH_CARD);
        File file;
        if (files.size() > 0) {
            Random random = new Random();
            int i = random.nextInt(files.size());
            file = files.get(i);
        } else {
            doAsyncDone();
            return;
        }
        resetCompare();
        final String name = BulkRegistPresenterImpl.getName(file.getName());
        showSlotCardState(getResources().getString(R.string.parse_begin) + " : " + name);
        Glide.with(DetectActivity.this).load(file).into(mImCompareWait);
        final Bitmap bitmap = ImageUtils.getBitmap(file);
        LogUtils.d(DetectPresenterImpl.HEAD, "extractFeatureASync Parsing");
        FaceDetectManager.extractFeatureASync(bitmap, new ExtractFeatureListener() {

            @Override
            public void onSuccess(DetectBean detectBean, ArrayList<Float> floats) {
                LogUtils.d(DetectPresenterImpl.HEAD, "extractFeatureASync success");
                showSlotCardState(getResources().getString(R.string.parse_success));
                if (isCompare) {
                    features.clear();
                    features.addAll(floats);
                    doAsyncDone();
                } else {
                    register(bitmap, detectBean, floats, name);
                }
            }

            @Override
            public void onError(final Status code) {
                LogUtils.d(DetectPresenterImpl.HEAD, "extractFeatureASync fail : " + code.toString());
                showSlotCardState(getResources().getString(R.string.parse_fail) + " : " + code.toString());
                doAsyncDone();
            }
        });
    }

    private void register(final Bitmap bitmap, final DetectBean detectBean, final ArrayList<Float> floats, final String name) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.registerUser(bitmap, detectBean, new RegisterBean(name), floats, new RegisterListener() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        LogUtils.d(DetectPresenterImpl.HEAD, "Register success");
                        showSlotCardState(getResources().getString(R.string.register_success) + " : " + userBean.name);
                        doAsyncDone();
                    }

                    @Override
                    public void onSimilarity(UserBean userBean) {
                        LogUtils.d(DetectPresenterImpl.HEAD, "Register User fail Similarity : " + userBean.name + " " + userBean.userId);
                    }

                    @Override
                    public void onError(Status status) {
                        LogUtils.d(DetectPresenterImpl.HEAD, "Register fail : " + status.toString());
                        showSlotCardState(getResources().getString(R.string.register_fail) + " : " + status.toString());
                        doAsyncDone();
                    }
                });
            }
        });
    }

    private void resetCompare() {
        mImCompareWait.setImageDrawable(null);
        mImCompareSucc.setImageDrawable(null);
        mTvCompareMsg.setText("");
        features.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
        DetectActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMaskView.unInit();
        detectList.clear();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mPresenter = null;
        mSurfaceView = null;
        ConfigLib.detectionFirstInitFlag = true;
        LogUtils.d(DetectPresenterImpl.HEAD, "RecognitionFace face destroy " + detectList.size());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ToastUtils.showShort(getResources().getString(R.string.reopen_activity));
        DetectActivity.this.finish();
    }

    @Override
    public void getCameraParameters() {
        mPreviewWidth = CameraInterface.getInstance().getPreviewWidth();
        mPreviewHeight = CameraInterface.getInstance().getPreviewHeight();
        FaceDetectManager.setDegree(AttConstants.CAMERA_DEGREE);
    }

    int initFrame = 1;
    int detectFrame = 4;
    int nowFrame = initFrame;
    boolean checkFrame = false;

    boolean check() {
        if (!checkFrame) {
            nowFrame++;
            dissmisDialog();
            if (nowFrame >= detectFrame) {
                nowFrame = initFrame;
                checkFrame = true;
            }
        }
        return checkFrame;
    }

    @Override
    public void cameraHasPreview(byte[] data, Camera camera) {
        LogUtils.d(DetectPresenterImpl.HEAD, "Begin -> ( w = " + mPreviewWidth + " h = " + mPreviewHeight + " size = " + data.length + " )");
        if (check()) {
            mPresenter.detectFaceData(data, mPreviewWidth, mPreviewHeight);
        }
    }

    @Override
    public void recognizeFace(final UserBean userBean) {
        Log.i("lxltest", "recognizeFace");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (userBean) {
                    String time = TimeUtils.millis2String(System.currentTimeMillis());
                    DetectFaceBean detectFaceBean = new DetectFaceBean(userBean, time);
                    if (detectList.size() != 0 && StringUtils.equals(detectList.get(0).getUserBean().name, userBean.name)) {
                        LogUtils.d(DetectPresenterImpl.HEAD, "RecognitionFace face set new + " + detectList.size());
                        detectList.set(0, detectFaceBean);
                    } else {
                        LogUtils.d(DetectPresenterImpl.HEAD, "RecognitionFace face add new + " + detectList.size());
                        detectList.add(0, detectFaceBean);
                    }
                    if (detectList.size() > AttConstants.DETECT_LIST_SIZE) {
                        List<DetectFaceBean> list = new ArrayList<>();
                        list.clear();
                        list.addAll(detectList);
                        detectList.clear();
                        detectList.addAll(list.subList(0, AttConstants.DETECT_LIST_SIZE));
                    }
                    mDetectAdapter.replaceData(detectList);
                }
            }
        });
    }

    @Override
    public void recognizeFaceNotMatch(UserBean userBean) {
        if (features.size() > 0) {
            LogUtils.d(DetectPresenterImpl.HEAD, "extractFeatureASync find recognizeFaceNotMatch");
            ArrayList<Float> floatArrayList = new ArrayList<>();
            floatArrayList.clear();
            floatArrayList.addAll(features);
            final float compare = FaceDetectManager.compareFeature(FeatureUtils.arrayListToFloat(floatArrayList), FeatureUtils.arrayListToFloat(userBean.features));
            LogUtils.d(DetectPresenterImpl.HEAD, "extractFeatureASync find recognizeFaceNotMatch : " + compare);
            if (compare > ConfigLib.featureThreshold) {
                Message message = Message.obtain();
                message.obj = userBean.headImage;
                message.what = MSG_FACE;
                mHandler.sendMessage(message);
                features.clear();
                showSlotCardState(getResources().getString(R.string.match_success) + " : " + compare);
            } else {
                showSlotCardState(getResources().getString(R.string.match_fail) + " : " + compare);
            }

        }
    }

    private void showSlotCardState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvCompareMsg.setText(state);
            }
        });
    }

    @Override
    public void detectNoFace() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DETECT_NO_FACE);
        }
    }

    @Override
    public void detectFail(Status status) {
        Message message = Message.obtain();
        message.what = MSG_DETECT_FAIL;
        message.obj = status;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void detectFace(final List<FaceBean> faceBeans) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (faceBeans) {
                    List<FaceBean> faceBeanList = new ArrayList<>();
                    faceBeanList.clear();
                    faceBeanList.addAll(faceBeans);
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        for (int i = 0; i < faceBeanList.size(); i++) {
                            FaceBean bean = faceBeanList.get(i);
                            if (bean.mUserBean != null && !StringUtils.isEmpty(bean.mUserBean.name)) {
                                String name = bean.mUserBean.name;
                                stringBuilder.append("< Find " + name + " >");
                                stringBuilder.append("\n");
                            } else {
                                String find = "";
                                if (ConfigLib.detectWithLiveness) {
                                    String liveState = "";
                                    if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.UNKNOWN) {
                                        liveState = "UNKNOWN";
                                    } else if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.FAKE) {
                                        liveState = "FAKE";
                                    }
                                    find = liveState;
                                } else {
                                    if (bean.mUserBean != null) {
                                        find = bean.mUserBean.compareScore + "";
                                    }
                                }
                                stringBuilder.append("< Find " + find + " >");
                                stringBuilder.append("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendDebugMessage(stringBuilder.toString());
                    mMaskView.drawRect(faceBeanList, mPreviewWidth, mPreviewHeight);
                }
            }
        });
    }

    public void sendDebugMessage(String s) {
        Message message = Message.obtain();
        message.what = MSG_DETECT_DATA;
        message.obj = s;
        mHandler.sendMessage(message);
    }

    public void openCamera(final int id, final int degree) {
        switch (AttConstants.CAMERA_PREVIEW_HEIGHT) {

            case 0:
                CameraInterface.getInstance().setPreViewSize(0, 0);
                break;

            case 480:
                CameraInterface.getInstance().setPreViewSize(640, 480);
                break;

            case 720:
                CameraInterface.getInstance().setPreViewSize(1280, 720);
                break;

            case 1080:
                CameraInterface.getInstance().setPreViewSize(1920, 1080);
                break;

        }
        Thread openThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CameraInterface.getInstance().doOpenCamera(id, degree);
            }
        };
        openThread.start();
    }

    private void exchangeCamera() {
        if (AttConstants.CAMERA_ID == 0) {
            AttConstants.CAMERA_ID = 1;
        } else {
            AttConstants.CAMERA_ID = 0;
        }
        AttApp.sp.edit().putInt(AttConstants.PREFS_CAMERA_ID, AttConstants.CAMERA_ID).commit();
        CameraInterface.getInstance().doStopCamera();
        openCamera(AttConstants.CAMERA_ID, AttConstants.PREVIEW_DEGREE);
    }
}
