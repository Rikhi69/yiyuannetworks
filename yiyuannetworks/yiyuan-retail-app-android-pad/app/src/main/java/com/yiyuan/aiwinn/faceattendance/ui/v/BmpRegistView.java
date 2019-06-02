package com.yiyuan.aiwinn.faceattendance.ui.v;

import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;

/**
 * com.aiwinn.faceattendance.ui.v
 * SDK_ATT
 * 2018/08/29
 * Created by LeoLiu on User
 */

public interface BmpRegistView {

    void registSucc(UserBean userBean);

    void registFail(Status status);

    void noFace();

    void registSucc(UserBean userBean, String name, String customerId, String customerGender);

    void registFail(Status status, String name, String customerId);

    void noFace(String name, String customerId);

}
