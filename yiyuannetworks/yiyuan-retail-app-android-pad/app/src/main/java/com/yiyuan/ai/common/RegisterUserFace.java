package com.yiyuan.ai.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.yiyuan.ai.AIConstants;
import com.yiyuan.ai.FaceUtil;
import com.yiyuan.aiwinn.faceattendance.ui.v.BmpRegistView;

public class RegisterUserFace implements BmpRegistView {
    private LogUtil log = LogUtil.getInstance();
    private Context context;

    public RegisterUserFace(Context context){
        this.context = context;
    }

    @Override
    public void registSucc(UserBean userBean) {

    }

    @Override
    public void registFail(Status status) {

    }

    @Override
    public void noFace() {

    }

    @Override
    public void registSucc(UserBean userBean, String name, String customerId, String customerGender) {
        FaceUtil.updateSpData(context,userBean.userId,name,customerId,customerGender);
        log.LogW("user register success:id=" +customerId+"。name="+name );
    }

    @Override
    public void registFail(Status status, String name, String customerId) {
        log.logE("user register fail:id=" +customerId+"。name="+name );
        //调用接口，提示数据失败
        String response = HttpUtil.getInstance().sendFailSyncCustomerService(customerId,"注册失败，状态为status="+status);
    }
    @Override
    public void noFace(String name, String customerId) {
        log.logE("user register noFace:id=" +customerId+"。name="+name );
        //调用接口，提示数据失败
        String response = HttpUtil.getInstance().sendFailSyncCustomerService(customerId,"注册失败，不能识别面部信息");
    }
}