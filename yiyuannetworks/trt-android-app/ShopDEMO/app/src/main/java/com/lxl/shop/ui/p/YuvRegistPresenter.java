package com.lxl.shop.ui.p;

import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.lxl.shop.ui.MyCallBack;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public interface YuvRegistPresenter {

    void registerFace(byte[] data, int w, int h);

    void registerFace(byte[] data, int w, int h, MyCallBack myCallBack);

    void dealFaceInfoFinish();

    void saveRegisterInfo(Bitmap bitmap, DetectBean faceInfoEx, RegisterBean registerBean);
}
