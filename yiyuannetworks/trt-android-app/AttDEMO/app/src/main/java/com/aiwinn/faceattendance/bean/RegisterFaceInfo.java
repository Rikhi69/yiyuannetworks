package com.aiwinn.faceattendance.bean;

import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.bean.DetectBean;

/**
 * com.aiwinn.faceattendance
 * SDK_ATT
 * 2018/08/25
 * Created by LeoLiu on User
 */

public class RegisterFaceInfo {
    private Bitmap mSrc;
    private DetectBean mDetectBean;

    public RegisterFaceInfo(Bitmap src , DetectBean detectBean) {
        mDetectBean = detectBean;
        mSrc = src;
    }


    public Bitmap getSrc() {
        return mSrc;
    }

    public void setSrc(Bitmap src) {
        mSrc = src;

    }

    public DetectBean getDetectBean() {
        return mDetectBean;
    }

    public void setDetectBean(DetectBean detectBean) {
        mDetectBean = detectBean;
    }
}
