package com.aiwinn.faceattendance.ui.p;

import android.graphics.Bitmap;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.faceattendance.ui.v.YuvRegistView;
import com.aiwinn.faceattendance.utils.FaceUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.DetectListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;

import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public class YuvRegistPresenterImpl implements YuvRegistPresenter {

    public static final String HEAD = "ATT_REGISTER";

    private YuvRegistView mView;
    private boolean mLandScape;

    public YuvRegistPresenterImpl(YuvRegistView view) {
        mView = view;
        mLandScape = ScreenUtils.isLandscape();
    }

    boolean tag = false;

    @Override
    public void registerFace(byte[] data, final int w, final int h) {
        if (tag) {
            return;
        }
        tag = true;
        LogUtils.d(HEAD,"enter : w = "+w +" h = "+h+" length = "+data.length);
        FaceDetectManager.detectFace(data, w, h, new DetectListener() {

            @Override
            public void onSuccess(List<DetectBean> detectBeanList) {
                if (detectBeanList.size() == 0) {
                    LogUtils.d(HEAD,"detect no face");
                    mView.noFace();
                    return;
                }
                DetectBean maxFace = FaceUtils.findMaxFace(detectBeanList);
                LogUtils.d(HEAD,"blur = "+maxFace.blur+" light = "+maxFace.light);
                if (!checkFaceIsRight(maxFace,w,h)) {
                    mView.notCenter();
                    return;
                }
                if (maxFace.blur > ConfigLib.blurRegisterThreshold && maxFace.light > ConfigLib.minRegisterBrightness) {
                    Bitmap bitmap = FaceDetectManager.yuvToBitmap(maxFace.faceData,w,h);
                    if (bitmap == null) {
                        mView.onError(Status.Nv21ToBitmapFail);
                    }else {
                        mView.faceInfo(bitmap, maxFace);
                    }
                }else{
                    dealFaceInfoFinish();
                }
            }

            @Override
            public void onError(Status status, String s) {
                LogUtils.d(HEAD,"detect face error = "+s);
                mView.onError(status);
            }
        });

    }
    boolean checkFaceIsRight(DetectBean detectBean,int width,int height){
        if (!mLandScape) {
            int bak = width;
            width = height;
            height = bak;
        }
        float x0 = detectBean.x0;
        float y0 = detectBean.y0;
        float x1 = detectBean.x1;
        float y1 = detectBean.y1;
        float w = x1 - x0;
        float h = y1 - y0;
        if (w > width
                || h > height
                || x0 < 0
                || y0 < 0
                || x1 > width
                || y1 > height) {
            LogUtils.d(HEAD, "detectBean.x1-detectBean.x0 = " + w);
            LogUtils.d(HEAD, "detectBean.y1-detectBean.y0 = " + h);
            LogUtils.d(HEAD, "detectBean.x1 = " + x1);
            LogUtils.d(HEAD, "detectBean.y1 = " + y1);
            LogUtils.d(HEAD, "image.getWidth() = " + width);
            LogUtils.d(HEAD, "image.getHeight() = " + height);
            LogUtils.d(HEAD, "check face fail : not center in pic");
            return false;
        }
        return true;
    }

    @Override
    public void dealFaceInfoFinish() {
        tag = false;
    }

    @Override
    public void saveRegisterInfo(final Bitmap bitmap, final DetectBean detectBean, final RegisterBean registerBean) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.registerUser(bitmap, detectBean, registerBean, new RegisterListener() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        mView.onRegisterSuccess(userBean);
                    }

                    @Override
                    public void onSimilarity(UserBean userBean) {
                        LogUtils.d(HEAD,"register face Error . Similarity : "+userBean.name+" "+userBean.userId);
                    }

                    @Override
                    public void onError(Status status) {
                        LogUtils.d(HEAD,"register face Error . status : "+status.toString());
                        mView.onRegisterError(status);
                    }

                });
            }
        });

    }

}
