package com.aiwinn.faceattendance.ui.v;

import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;

/**
 * com.aiwinn.faceattendance.ui.v
 * SDK_ATT
 * 2018/08/29
 * Created by LeoLiu on User
 */

public interface YuvRegistView {

    void onError(Status status);

    void noFace();

    void notCenter();

    void faceInfo(Bitmap src, DetectBean maxFace);

    void onRegisterSuccess(UserBean userBean);

    void onRegisterError(Status status);

}
