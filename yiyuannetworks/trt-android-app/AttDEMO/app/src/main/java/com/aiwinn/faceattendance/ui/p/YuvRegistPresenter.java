package com.aiwinn.faceattendance.ui.p;

import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public interface YuvRegistPresenter {

    void registerFace(byte[] data, int w, int h);

    void dealFaceInfoFinish();

    void saveRegisterInfo(Bitmap bitmap, DetectBean faceInfoEx, RegisterBean registerBean);
}
