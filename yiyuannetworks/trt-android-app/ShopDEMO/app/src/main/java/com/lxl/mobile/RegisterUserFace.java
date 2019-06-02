package com.lxl.mobile;

import android.content.Context;
import android.content.SharedPreferences;

import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.ui.v.BmpRegistView;
import com.lxl.shop.utils.LogUtil;

import java.util.ArrayList;

class RegisterUserFace implements BmpRegistView {
    private LogUtil log = LogUtil.getInstance();
    private Context context;

    public RegisterUserFace(Context context){
        this.context = context;
    }

    @Override
    public void registSucc(UserBean userBean, String name, String customerId, String customerGender) {
        SharedPreferences customerIdSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
        SharedPreferences.Editor idEdit = customerIdSP.edit();
        SharedPreferences customerNameSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0);
        SharedPreferences customerGenderSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0);
        SharedPreferences.Editor nameEdit = customerNameSP.edit();
        SharedPreferences.Editor genderEdit = customerGenderSP.edit();
        idEdit.putString(userBean.userId, customerId);
        idEdit.apply();
        nameEdit.putString(userBean.userId, name);
        nameEdit.apply();
        genderEdit.putString(userBean.userId, customerGender);
        genderEdit.apply();
        log.LogW("user register success:id=" +customerId+"。name="+name );
        log.LogW("user register success:id=" +customerId+"。name="+name );
    }
    @Override
    public void registFail(Status status, String name, String customerId) {
        log.logE("user register fail:id=" +customerId+"。name="+name );
        //调用接口，提示数据失败
        String response = StockSender.getInstance().sendFailSyncCustomerService(customerId,"注册失败，状态为status="+status);
    }
    @Override
    public void noFace(String name, String customerId) {
        log.logE("user register noFace:id=" +customerId+"。name="+name );
        //调用接口，提示数据失败
        String response = StockSender.getInstance().sendFailSyncCustomerService(customerId,"注册失败，不能识别面部信息");
    }
}