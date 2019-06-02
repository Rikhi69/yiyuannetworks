package com.lxl.shop.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.ui.p.BmpRegistPresenter;
import com.lxl.shop.ui.p.BmpRegistPresenterImpl;
import com.lxl.shop.ui.v.BmpRegistView;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lxl.shop.common.ShopConfig.SYNC_CUSTOMER_TIME_INTERVAL;

/**
 * Created by yanglei on 2018/11/27.
 */

public class SyncCustomerServiceImpl implements BmpRegistView {

    LogUtil log;
    BmpRegistPresenter mPresenter;
    SharedPreferences customerIdSP;//key:userBean.userId value:customerId
    SharedPreferences customerNameSP;
    SharedPreferences customerGenderSP;

    public SyncCustomerServiceImpl(Context context) {
        log = LogUtil.getInstance(context);
        customerIdSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
        customerNameSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0);
        customerGenderSP = context.getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0);
        mPresenter = new BmpRegistPresenterImpl(this);
    }

    public int syncCustomerService() {
        //同步
        List<UserBean> userBeans = FaceDetectManager.queryAll();
        if (userBeans == null) {
            userBeans = new ArrayList<>();
        }

        //根据faceID去本地库查找customerId
        List<String> customerIdList = new ArrayList<>();
        for (UserBean userBean : userBeans) {
            String customerId = customerIdSP.getString(userBean.userId, "");//如果没有记录就清除
            if (StringUtil.emptyOrNull(customerId)) {
                //如果不存在则说明注册未成功，则删除
                FaceDetectManager.deleteByUserInfo(userBean);
                continue;
            }
            if(userBean.localImagePath != null) {
                File file = new File(userBean.localImagePath);
                if (!file.exists()) {
                    //如果不存在则说明已删除
                    FaceDetectManager.deleteByUserInfo(userBean);
                    continue;
                }
            }
            customerIdList.add(customerId);
        }

        List<CustomerModel> customerModels = new ArrayList<>();
        try {
            customerModels = StockSender.getInstance().sendSyncCustomerService(customerIdList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (customerModels.size() == 0) {
            log.LogW("customerModels.size()");
            return SYNC_CUSTOMER_TIME_INTERVAL;
        }
        for (final CustomerModel customerModel : customerModels) {
            if (StringUtil.emptyOrNull(customerModel.imgUrl)) {
                log.LogW("register:" + customerModel.name);
                continue;
            }
            if (!customerIdList.contains(customerModel.customerId)) {
                log.LogW("download img:" + customerModel.name+",imgUrl:"+customerModel.imgUrl);
                final Bitmap bitmap = StockSender.getInstance().uploadCustomer(customerModel.imgUrl);
                if (bitmap == null) {
                    log.LogW("download img fail:url" + customerModel.imgUrl);
                    continue;
                }
                mPresenter.registUser(bitmap, customerModel.name, customerModel.customerId, customerModel.gender);
                log.LogW("register:" + customerModel.name);
            }
        }
        return SYNC_CUSTOMER_TIME_INTERVAL;
    }

    @Override
    public void registSucc(UserBean userBean, String customerName, String customerId, String customerGender) {
        //记录userId和customerId的mapping关系
        SharedPreferences.Editor idEdit = customerIdSP.edit();
        SharedPreferences.Editor nameEdit = customerNameSP.edit();
        SharedPreferences.Editor genderEdit = customerGenderSP.edit();
        idEdit.putString(userBean.userId, customerId);
        idEdit.apply();

        nameEdit.putString(userBean.userId, customerName);
        nameEdit.apply();

        genderEdit.putString(userBean.userId, customerGender);
        genderEdit.apply();
        log.LogW("registSucc:" + userBean.name);
    }

    @Override
    public void registFail(Status status, String name, String customerId) {
        log.LogW("registFail:" + status.name() + ",name:" + name + ",customerId:" + customerId);
    }

    @Override
    public void noFace(String name, String customerId) {
        log.LogW("noFace:name:" + name + ",customerId:" + customerId);
    }
}
