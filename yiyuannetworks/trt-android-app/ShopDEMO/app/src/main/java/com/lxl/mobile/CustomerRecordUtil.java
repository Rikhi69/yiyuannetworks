package com.lxl.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.faceSDK.AgeInfo;
import com.aiwinn.faceSDK.FaceInfoBean;
import com.aiwinn.faceSDK.GenderInfo;
import com.aiwinn.faceSDK.SDKManager;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.module.attdb.bean.User;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.ui.p.BmpRegistPresenter;
import com.lxl.shop.ui.p.BmpRegistPresenterImpl;
import com.lxl.shop.ui.v.BmpRegistView;
import com.lxl.shop.utils.IOHelper;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.CustomerRecongizeResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lxl.shop.common.ShopConfig.WELCOME_SHOW_TIME;

public class CustomerRecordUtil {
    static int pullDataPageSize = 100;
    static LogUtil log = LogUtil.getInstance();
    static LogUtil logSocket = LogUtil.getInstance();
    static SharedPreferences customerIdSP;
    public static void recognitionNewCustomerHandler(Context mContext,UserBean userBean, DetectBean detectBean){
        recognitionNewCustomerHandler(mContext,userBean,detectBean,null);
    }
    //新用户记录保存
    public static void recognitionNewCustomerHandler(Context mContext, UserBean userBean, DetectBean detectBean, String faceCoordinates) {
        String localImagePath = userBean.localImagePath;
        if (StringUtil.emptyOrNull(userBean.localImagePath)) {
            if (userBean.headImage != null) {
                localImagePath = IOHelper.saveBitmap(mContext, userBean.headImage);
            } else {
                log.logE("userName:" + userBean.name + ",localImagePath is null");
                return;
            }
        }
//        File file = new File(localImagePath);
//        String imgUrl = StockSender.getInstance().toUploadFile(file, new HashMap<String, String>());
//        Bitmap bitmap = ImageUtils.getBitmap(file);
//        FaceInfoBean faceInfoBean = SDKManager.getInstance().getFaceDetectBeanByBitmap(bitmap);
//        AgeInfo[] ageInfos = faceInfoBean.getAgeInfos();
//        GenderInfo[] genderInfos = faceInfoBean.getGenderInfos();
//
//        if (StringUtil.emptyOrNull(imgUrl)) {
//            return;
//        }
//        if (ageInfos == null) {
//            return;
//        }
//        if (genderInfos == null) {
//            return;
//        }
//        ArrayList<Float> floatList = new ArrayList<>();
//        if (userBean.features != null && userBean.features.size() > 0) {
//            floatList = userBean.features;
//        } else {
//            float[] floats = FaceDetectManager.extractFeature(bitmap, detectBean);
//            for (int i = 0; i < floats.length; i++) {
//                floatList.add(floats[i]);
//            }
//        }
//        String faceId = JSON.toJSONString(floatList);
//        int age = (int) ageInfos[0].age;
//        String gender = genderInfos[0].gender > 0.5F ? "男" : "女";
//        StockSender.getInstance().sendAddNewCustomerRecord(imgUrl, faceId, age, gender,faceCoordinates);
    }


    public static void recognitionOldCustomerHandler(Context mContext, final UserBean userBean, Handler mHandler, List<CustomerModel> welcomeCustomerList
            ,RelativeLayout mWelcomeView, LinearLayout mWelcomeList, ViewPager mViewPager, SurfaceView mSurfaceView){
        recognitionOldCustomerHandler(mContext,userBean,mHandler,welcomeCustomerList,
                mWelcomeView,mWelcomeList,mViewPager,mSurfaceView,null);
    }
    public static void recognitionOldCustomerHandler(final Context mContext,final UserBean userBean, final Handler mHandler, final List<CustomerModel> welcomeCustomerList,
                                                     final RelativeLayout mWelcomeView, final LinearLayout mWelcomeList, final ViewPager mViewPager, final SurfaceView mSurfaceView,final String faceCoordinates) {
        //发送请求给服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String customerId = mContext.getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0).getString(userBean.userId, "");
                final String customerName = mContext.getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0).getString(userBean.userId, "");
                final String customerGender = mContext.getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0).getString(userBean.userId, "");
                final String userBeanLocalImg = userBean.localImagePath;

                //如果匹配到已经在展示列表当中，就返回，不在展示以及不发识别服务
                for (CustomerModel customerModel : welcomeCustomerList) {
                    if (customerId.equalsIgnoreCase(customerModel.customerId)) {
                        return;
                    }
                }
                if (StringUtil.emptyOrNull(customerId)) {
                    return;
                }
                //发送服务通知服务记录一下识别到了老客
                //CustomerRecongizeResponse recongizeModel = StockSender.getInstance().sendAddRecongizeCustomer(customerId,faceCoordinates);
                final CustomerModel customerModel = StockSender.getInstance().getCustomerById(customerId);
                if (StringUtil.emptyOrNull(customerModel.customerId)) {
                    log.logE("customerId为空，faceId:" + userBean.userId);
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addCustomerModel(mContext,mHandler,customerId, customerName, customerGender, userBeanLocalImg,customerModel.imgUrl,welcomeCustomerList
                                ,mWelcomeView,mWelcomeList,mViewPager,mSurfaceView);
                    }
                });
            }
        }).start();
    }


    public static void addCustomerModel(Context mContext, Handler mHandler, String customerId, String customerName, String gender, String userBeanLocalImg, String imgUrl, List<CustomerModel> welcomeCustomerList, RelativeLayout mWelcomeView, LinearLayout mWelcomeList, ViewPager mViewPager, SurfaceView mSurfaceView) {
        if (StringUtil.emptyOrNull(customerId)) {
            return;
        }
        CustomerModel currentCustomer = null;
        for (CustomerModel customer : welcomeCustomerList) {
            if (customerId.equals(customer.customerId)) {
                currentCustomer = customer;
                //移动顺序
                break;
            }
        }
        if (currentCustomer == null) {
            currentCustomer = new CustomerModel();
            currentCustomer.customerId = customerId;
            currentCustomer.gender = gender;
            currentCustomer.name = customerName;
            currentCustomer.userModel.localImagePath = imgUrl;
            if (welcomeCustomerList.size() >= 5) {
                welcomeCustomerList.remove(0);
            }
            welcomeCustomerList.add(currentCustomer);
        } else {
            welcomeCustomerList.remove(currentCustomer);
            welcomeCustomerList.add(currentCustomer);
        }
        refreshCustomerModel(mContext,mHandler,welcomeCustomerList,mWelcomeView,mWelcomeList,mViewPager,mSurfaceView);
    }

    public static void refreshCustomerModel(Context mContext, Handler mHandler, final List<CustomerModel> welcomeCustomerList, final RelativeLayout mWelcomeView, LinearLayout mWelcomeList, final ViewPager mViewPager, final SurfaceView mSurfaceView) {
        if (welcomeCustomerList.size() == 0) {
            showViewPager(mViewPager,mSurfaceView,mWelcomeView);
            return;
        }
        mWelcomeView.setVisibility(View.VISIBLE);
        mWelcomeList.removeAllViews();
        for (CustomerModel customerModel : welcomeCustomerList) {
            CharSequence welcomeDesc = getWelcomeDesc(mContext,customerModel.name, customerModel.gender);
            View inflate = View.inflate(mContext, R.layout.shop_welcome_item_view, null);
            TextView welcomeItemText = inflate.findViewById(R.id.welcome_item_text);
            ImageView imageView = inflate.findViewById(R.id.welcome_item_icon);
            Glide.with(mContext).load(customerModel.userModel.localImagePath).into(imageView);
            welcomeItemText.setText(welcomeDesc);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            mWelcomeList.addView(inflate, lp);
        }

        final long tontinueTime = System.currentTimeMillis() + WELCOME_SHOW_TIME;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - tontinueTime) >= 0) {
                    showViewPager(mViewPager,mSurfaceView,mWelcomeView);
                    welcomeCustomerList.clear();
                }
            }
        }, WELCOME_SHOW_TIME);
    }


    public static void showViewPager(ViewPager mViewPager, SurfaceView mSurfaceView, RelativeLayout mWelcomeView) {
        hideWelComeView(mWelcomeView);
        mViewPager.setVisibility(View.VISIBLE);
        mSurfaceView.getLayoutParams().height = 1;
    }
    public static void hideWelComeView(RelativeLayout mWelcomeView) {
        mWelcomeView.setVisibility(View.GONE);
    }

    private static CharSequence getWelcomeDesc(Context mContext, String customerName, String gender) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String genderStr = "女".equals(gender) ? "女士" : "先生";
        stringBuilder.append("欢迎 ");
        stringBuilder.append(customerName);
        stringBuilder.append(genderStr);

        int index = 3 + customerName.length();

        stringBuilder.setSpan(new TextAppearanceSpan(mContext, R.style.text_30_ffffff), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new TextAppearanceSpan(mContext, R.style.text_50_ffffff_b), 3, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new TextAppearanceSpan(mContext, R.style.text_30_ffffff), index, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }



    //推送指令进行人脸库数据处理，重置人脸库数据
    public static void initDataByServer(Context mContext){
        BmpRegistPresenter mPresenter  = new BmpRegistPresenterImpl(new RegisterUserFace(mContext));
        FaceDetectManager.deleteAll();
        List<CustomerModel> customerModels = new ArrayList<>();
        try {
            //customerModels = StockSender.getInstance().getCustomerListByIds(new ArrayList<String>());
            int currentPage = 1;
            int pageSize = 100;
            while((customerModels!=null && customerModels.size() >0)|| currentPage == 1){

                try {
                    customerModels = StockSender.getInstance().sendSyncCustomerService(currentPage,pageSize,null);
                    currentPage++;
                    for (final CustomerModel customerModel : customerModels) {
                        registerCustomer("pullData", mPresenter, customerModel);
                    }
                } catch (Exception e) {
                    showToast(mContext,"pullData 数据请求出错");
                }
            }

        } catch (Exception e) {
            showToast(mContext,"initData 数据请求出错");
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
                customerModels = StockSender.getInstance().getCustomerListByIds(customIdLIstTemp);
            } catch (Exception e) {
                showToast(mContext,"pullData数据解析出错");
            }
            if(customerModels == null || customerModels.size() == 0){
                for (String id : customIdLIstTemp) {
                    String response = StockSender.getInstance().sendFailSyncCustomerService(id,"跟据id批量获取数据失败");
                }
            }
            for (final CustomerModel customerModel : customerModels) {
                registerCustomer("pullData",mPresenter,customerModel);
            }
        }

    }

    private static void registerCustomer(String tag,BmpRegistPresenter mPresenter,CustomerModel customerModel){
        if(StringUtil.emptyOrNull(customerModel.customerId)){
            logSocket.logE(tag+" user customerId is null:id=" +customerModel.customerId+"。name="+customerModel.name );
            return ;
        }
        if (StringUtil.emptyOrNull(customerModel.imgUrl)) {
            logSocket.logE(tag+" user imgUrl is null:id=" +customerModel.customerId+"。name="+customerModel.name );
            return;
        }
        final Bitmap bitmap = StockSender.getInstance().uploadCustomer(customerModel.imgUrl);
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
            showToast(mContext,"saveData数据解析出错");
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
            showToast(mContext,"saveData数据解析出错");
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

    private static String getUserIdByCustomId(Context mContext,String customerId) {
        if(customerIdSP == null) {
            customerIdSP = mContext.getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0);
        }
        Map<String,Object> map = (Map<String, Object>) customerIdSP.getAll();
        for (String key : map.keySet()) {
            if(map.get(key)!= null && map.get(key).equals(customerId)){
                return key;
            }
        }
        return "";
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

    public static void showToast(final Context context,final String content){
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context,content,Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });
    }
}
