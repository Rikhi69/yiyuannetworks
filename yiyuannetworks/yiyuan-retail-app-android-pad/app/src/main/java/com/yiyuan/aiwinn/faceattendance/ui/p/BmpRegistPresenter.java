package com.yiyuan.aiwinn.faceattendance.ui.p;

import android.graphics.Bitmap;

import java.io.File;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/29
 * Created by LeoLiu on User
 */

public interface BmpRegistPresenter {

    void registUser(File file, String name);

    void registUser(File file, String name, String customerId, String customerGender);

    void registUser(Bitmap bitmap, String name, String customerId, String customerGender);

}
