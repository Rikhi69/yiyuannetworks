package com.lxl.shop.ui.p;

import android.graphics.Bitmap;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.DetectListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;
import com.lxl.shop.ui.v.BmpRegistView;
import com.lxl.shop.utils.FaceUtils;

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
    public void registUser(File file, final String name, final String customerId, final String customerGender) {
        Bitmap bitmap = ImageUtils.getBitmap(file);
        registUser(bitmap, name, customerId, customerGender);
    }

    @Override
    public void registUser(final Bitmap bitmap, final String customerName, final String customerId, final String customerGender) {
        LogUtils.d(HEAD, "start detect face by bmp");
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.detectFace(bitmap, new DetectListener() {

                    @Override
                    public void onSuccess(List<DetectBean> detectBeanList) {
                        if (detectBeanList.size() > 0) {
                            LogUtils.d(HEAD, "find face size > 0");
                            regist(detectBeanList, customerName, customerId, customerGender);
                        } else {
                            mView.noFace(customerName, customerId);
                        }
                    }

                    @Override
                    public void onError(Status status, String s) {
                        mView.registFail(status, customerName, customerId);
                    }
                });
            }
        });
    }

    private void regist(List<DetectBean> list, final String customerName, final String customerId, final String customerGender) {
        LogUtils.d(HEAD, "start register face");
        DetectBean maxFace = FaceUtils.findMaxFace(list);
        RegisterBean registerBean = new RegisterBean(customerName);
        FaceDetectManager.registerUser(maxFace.faceBitmap, maxFace, registerBean, new RegisterListener() {
            @Override
            public void onSuccess(UserBean userBean) {
                LogUtils.d(HEAD, "register face done . result : " + userBean.name);
                mView.registSucc(userBean, customerName, customerId, customerGender);
            }

            @Override
            public void onSimilarity(UserBean userBean) {
                LogUtils.d(HEAD, "register face Error . Similarity : " + userBean.name + " " + userBean.userId);
            }

            @Override
            public void onError(Status status) {
                LogUtils.d(HEAD, "register face Error . status : " + status.toString());
                mView.registFail(status, customerName, customerId);
            }
        });

    }

}
