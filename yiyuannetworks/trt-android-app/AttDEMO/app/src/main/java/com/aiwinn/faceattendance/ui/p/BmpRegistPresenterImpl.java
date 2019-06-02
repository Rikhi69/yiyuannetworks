package com.aiwinn.faceattendance.ui.p;

import android.graphics.Bitmap;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.faceattendance.ui.v.BmpRegistView;
import com.aiwinn.faceattendance.utils.FaceUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.DetectListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;

import java.io.File;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/29
 * Created by LeoLiu on User
 */

public class BmpRegistPresenterImpl implements BmpRegistPresenter {

    public static final String HEAD = "ATT_BMP_REGIST";

    private final BmpRegistView mView;

    public BmpRegistPresenterImpl(BmpRegistView view) {
        mView = view;
    }

    @Override
    public void registUser(File file, final String name) {
        final Bitmap bitmap = ImageUtils.getBitmap(file);
        LogUtils.d(HEAD,"start detect face by bmp");
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.detectFace(bitmap, new DetectListener() {

                    @Override
                    public void onSuccess(List<DetectBean> detectBeanList) {
                        if (detectBeanList.size() > 0) {
                            LogUtils.d(HEAD,"find face size > 0");
                            regist(detectBeanList,name);
                        }else {
                            mView.noFace();
                        }
                    }

                    @Override
                    public void onError(Status status, String s) {
                        mView.registFail(status);
                    }
                });
            }
        });
    }

    private void regist(List<DetectBean> list, String name) {
        LogUtils.d(HEAD,"start register face");
        DetectBean maxFace = FaceUtils.findMaxFace(list);
        RegisterBean registerBean = new RegisterBean(name);
        FaceDetectManager.registerUser(maxFace.faceBitmap, maxFace, registerBean, new RegisterListener() {
            @Override
            public void onSuccess(UserBean userBean) {
                LogUtils.d(HEAD,"register face done . result : "+userBean.name);
                mView.registSucc(userBean);
            }

            @Override
            public void onSimilarity(UserBean userBean) {
                LogUtils.d(HEAD,"register face Error . Similarity : "+userBean.name+" "+userBean.userId);
            }

            @Override
            public void onError(Status status) {
                LogUtils.d(HEAD,"register face Error . status : "+status.toString());
                mView.registFail(status);
            }
        });

    }

}
