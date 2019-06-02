package com.yiyuan.ai;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.Toast;

import com.aiwinn.base.utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.alibaba.fastjson.JSON;
import com.yiyuan.ai.common.HttpUtil;
import com.yiyuan.ai.common.LogSocketUtil;
import com.yiyuan.ai.common.RegisterUserFace;
import com.yiyuan.ai.common.StringUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.aiwinn.faceattendance.ui.p.BmpRegistPresenter;
import com.yiyuan.aiwinn.faceattendance.ui.p.BmpRegistPresenterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketUserUtil {
    static int pullDataPageSize = 100;
    static LogSocketUtil logSocket = LogSocketUtil.getInstance();
    static SharedPreferences customerIdSP;
    static long tontinueTime = 0;


    //推送指令进行人脸库数据处理，重置人脸库数据
    public static void initDataByServer(Context mContext){
        BmpRegistPresenter mPresenter  = new BmpRegistPresenterImpl(new RegisterUserFace(mContext));
        FaceDetectManager.deleteAll();
        List<CustomerModel> customerModels = new ArrayList<>();
        try {
            //customerModels = StockSender.getInstance().getCustomerListByIds(new ArrayList<String>());
            int currentPage = 1;
            int pageSize = 100;
            while((customerModels!=null && customerModels.size() >0)|| currentPage ==1){

                try {
                    customerModels = HttpUtil.getInstance().sendSyncCustomerService(currentPage,pageSize,null);
                    currentPage++;
                    for (final CustomerModel customerModel : customerModels) {
                        registerCustomer("initData", mPresenter, customerModel);
                    }
                } catch (Exception e) {
                    showToast(mContext,"initData 数据请求出错："+e.getMessage());
                }
            }

        } catch (Exception e) {
            showToast(mContext,"initData 数据请求出错："+e.getMessage());
        }
        for (final CustomerModel customerModel : customerModels) {
            registerCustomer("initData",mPresenter,customerModel);
        }
    }

    public static void pullDataByServer(Context mContext, List<String> customIdList) {
        if(customIdList == null || customIdList.size() < 1){
            showToast(mContext,"pullData data is null。customIdList size is zero");
        }
        BmpRegistPresenter mPresenter  = new BmpRegistPresenterImpl(new RegisterUserFace(mContext));
        List<CustomerModel> customerModels = new ArrayList<>();
        int page  = 0;
        int currentPage = 0;
        page = customIdList.size()/pullDataPageSize;
        if(customIdList.size()%pullDataPageSize != 0){
            page++;
        }
        for (int i = currentPage; i< page;i++ ) {
            List<String> customIdLIstTemp = new ArrayList<>();
            try {
                customIdLIstTemp = customIdList.subList(currentPage*pullDataPageSize,(currentPage+1)*pullDataPageSize>customIdList.size()?customIdList.size():(currentPage+1)*pullDataPageSize);
                //此处进行数据批量获取
                customerModels = HttpUtil.getInstance().getCustomerListByIds(customIdLIstTemp);
            } catch (Exception e) {
                showToast(mContext,"pullData："+e.getMessage());
            }
            if(customerModels == null || customerModels.size() == 0){
                for (String id : customIdLIstTemp) {
                    String response = HttpUtil.getInstance().sendFailSyncCustomerService(id,"跟据id批量获取数据失败");
                }
            }
            for (final CustomerModel customerModel : customerModels) {
                registerCustomer("pullData",mPresenter,customerModel);
            }
        }

    }

    private static void registerCustomer(String tag, BmpRegistPresenter mPresenter, CustomerModel customerModel){
        if(StringUtil.emptyOrNull(customerModel.customerId)){
            logSocket.logE(tag+" user customerId is null:id=" +customerModel.customerId+"。name="+customerModel.name );
            return ;
        }
        if (StringUtil.emptyOrNull(customerModel.imgUrl)) {
            logSocket.logE(tag+" user imgUrl is null:id=" +customerModel.customerId+"。name="+customerModel.name );
            return;
        }
        final Bitmap bitmap = HttpUtil.getInstance().uploadCustomer(customerModel.imgUrl);
        if (bitmap == null) {
            logSocket.logE(tag+" download img fail:imgUrl" + customerModel.imgUrl);
            return;
        }
        mPresenter.registUser(bitmap, customerModel.name, customerModel.customerId, customerModel.gender);
        logSocket.LogW(tag+" register user finish:" + customerModel.name);
    }

    public static void saveDataByServer(Context mContext, String object) {
        if(StringUtil.emptyOrNull(object)){
            logSocket.logE("saveData object is null" );
            return;
        }
        BmpRegistPresenter mPresenter  = new BmpRegistPresenterImpl(new RegisterUserFace(mContext));
        CustomerModel customerModel = null;
        try {
            customerModel = JSON.parseObject(object, CustomerModel.class);
        }catch (Exception e){
            showToast(mContext,"saveData数据解析出错："+e.getMessage());
        }
        List<UserBean> userBeanList = FaceDetectManager.queryByUserId(getUserIdByCustomId(mContext,customerModel.customerId));
        if(userBeanList.size() >0 ) {
            FaceDetectManager.deleteByUserInfo(userBeanList);
        }
        registerCustomer("saveData",mPresenter,customerModel);
    }

    public static void updateDataByServer(Context mContext, String object) {
        if(StringUtil.emptyOrNull(object)){
            logSocket.logE("updateData object is null" );
            return;
        }
        BmpRegistPresenter mPresenter  = new BmpRegistPresenterImpl(new RegisterUserFace(mContext));
        CustomerModel customerModel = null;
        try {
            customerModel = JSON.parseObject(object, CustomerModel.class);
        }catch (Exception e){
            showToast(mContext,"saveData数据解析出错："+e.getMessage());
        }
        if(StringUtil.emptyOrNull(customerModel.customerId)){
            logSocket.logE("updateData user customerId is null:id=" +customerModel.customerId+"。name="+customerModel.name );
            return;
        }else{
            List<UserBean> userBeanList = FaceDetectManager.queryByUserId(getUserIdByCustomId(mContext,customerModel.customerId));
            FaceDetectManager.deleteByUserInfo(userBeanList);
        }
        registerCustomer("updateData",mPresenter,customerModel);
    }

    public static void DeleteByServer(Context mContext, List<String> customIdList) {
        if(customIdList == null || customIdList.size() == 0){
            logSocket.logE("deleteData object is null" );
            return;
        }
        for (String id : customIdList) {
            String customId = id;
            List<UserBean> userBeanList = FaceDetectManager.queryByUserId(getUserIdByCustomId(mContext,customId));
            FaceDetectManager.deleteByUserInfo(userBeanList);
        }
    }
    public static String getUserIdByCustomId(Context mContext, String customerId) {
        if(customerIdSP == null) {
            customerIdSP = mContext.getSharedPreferences(AIConstants.SP_Register_CustomerId, 0);
        }
        Map<String,Object> map = (Map<String, Object>) customerIdSP.getAll();
        for (String key : map.keySet()) {
            if(map.get(key)!= null && map.get(key).equals(customerId)){
                return key;
            }
        }
        return "";
    }
    public static String getCustomIdByUserId(Context mContext, String userId) {
        if(customerIdSP == null) {
            customerIdSP = mContext.getSharedPreferences(AIConstants.SP_Register_CustomerId, 0);
        }
        return customerIdSP.getString(userId,"");
    }

    public static void showToast(final Context context, final String content){
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context,content, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });
    }
}
