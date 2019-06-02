package com.yiyuan.ai;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.RegisterListener;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.view.FaceView;
import com.yiyuan.aiwinn.faceattendance.bean.RegisterFaceInfo;

import java.util.UUID;

/**
 * Created by wangyu on 2019/4/11.
 */

public class FaceUtil {
    public static void registerUserAI(RegisterFaceInfo faceInfo,final FaceView faceView){
        RegisterBean registerBean = new RegisterBean(UUID.randomUUID().toString());
        FaceDetectManager.registerUser(faceInfo.getSrc(), faceInfo.getDetectBean(), registerBean, new RegisterListener() {
            @Override
            public void onSuccess(UserBean userBean) {
                userBean.name="新会员"+userBean.userId;
                FaceDetectManager.updateUser(userBean);
                faceView.success(userBean);
            }

            @Override
            public void onSimilarity(UserBean userBean) {
                faceView.error("您已注册");
                DialogUtil.dissmisDialog();
            }

            @Override
            public void onError(Status status) {
                if(!Status.HaveRegisteredThePerson.toString().equals(status.toString())){
                    faceView.error("人脸识别失败，请重新尝试");
                }
                DialogUtil.dissmisDialog();
            }
        });
    }

    public static void updateSpData(Context context, String userId, String name, String customerId, String customerGender){
        SharedPreferences customerIdSP = context.getSharedPreferences(AIConstants.SP_Register_CustomerId, 0);
        SharedPreferences.Editor idEdit = customerIdSP.edit();
        SharedPreferences customerNameSP = context.getSharedPreferences(AIConstants.SP_Register_CustomerName, 0);
        SharedPreferences customerGenderSP = context.getSharedPreferences(AIConstants.SP_Register_CustomerGender, 0);
        SharedPreferences.Editor nameEdit = customerNameSP.edit();
        SharedPreferences.Editor genderEdit = customerGenderSP.edit();
        idEdit.putString(userId, customerId);
        idEdit.apply();
        nameEdit.putString(userId, name);
        nameEdit.apply();
        genderEdit.putString(userId, customerGender);
        genderEdit.apply();
    }
}
