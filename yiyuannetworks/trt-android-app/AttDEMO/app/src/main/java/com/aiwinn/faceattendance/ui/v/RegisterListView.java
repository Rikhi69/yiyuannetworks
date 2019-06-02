package com.aiwinn.faceattendance.ui.v;

import com.aiwinn.facedetectsdk.bean.UserBean;

/**
 * com.aiwinn.faceattendance.ui.v
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public interface RegisterListView {

    void deleteFace(int index,UserBean face);

    void deleteDone(int index,UserBean face);

    void deleteFail(UserBean face);

    void deleteAllDone(boolean result);

}
