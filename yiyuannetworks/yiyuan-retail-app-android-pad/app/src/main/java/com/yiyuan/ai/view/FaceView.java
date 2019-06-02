package com.yiyuan.ai.view;

import com.aiwinn.facedetectsdk.bean.UserBean;

/**
 * Created by wangyu on 2019/4/11.
 */

public interface FaceView {
    public void success(UserBean userBean);
    public void error(String msg);
}
