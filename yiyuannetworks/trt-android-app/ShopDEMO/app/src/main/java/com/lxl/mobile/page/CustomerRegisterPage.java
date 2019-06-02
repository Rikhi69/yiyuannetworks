package com.lxl.mobile.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aiwinn.base.util.StringUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.detectTracker.DestDetectionTracker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.ui.m.YuvRegistActivity;
import com.lxl.shop.utils.DeviceUtil;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StockUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.utils.ValidateUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.UserModel;
import com.lxl.shop.widget.StockTextView;
import com.lxl.shop.widget.StockTitleView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.validation.Validator;

import static com.lxl.shop.common.ShopConstants.RegisterCode;

/**
 * Created by yanglei on 2018/11/23.
 */

public class CustomerRegisterPage extends PageInitActivity implements View.OnClickListener {

    public static final int SELECT_UN = 0;
    public static final int SELECT_MAN = 1;
    public static final int SELECT_WOMAN = 2;

    public static final String imageFilePathTemp = "1gdygwuiqgdqjkwbdhvuawgduohqbbe12dahioqh";

    Button mSubmit;

    ImageView mUserIconViewImg;

    StockTextView mGenderMan;
    StockTextView mGenderWoman;
    LinearLayout mShopRegisterContainer;
    CustomerModel mCustomerModel = new CustomerModel();
    LogUtil log;
    boolean isSubmitting;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = LogUtil.getInstance(this);
        setContentView(R.layout.shop_page_register_customer);
        initView();
        initAction();
        initData();
        initListeners();
    }

    @Override
    protected void initData() {
        //以id为配置
        mSubmit.requestFocus();
        addRegisterItem("会员名称", R.drawable.register_username, "string");
        addRegisterItem("电话号码", R.drawable.register_phone, "number");
        addRegisterItem("邮箱", R.drawable.register_email, "string");
        addRegisterItem("年龄", R.drawable.register_date, "number");
        addRegisterItem("地址", R.drawable.register_address, "string");
    }

    @Override
    protected void initListeners() {
        mSubmit.setOnClickListener(this);
        mUserIconViewImg.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        mGenderMan.setOnClickListener(this);
        mGenderWoman.setOnClickListener(this);
    }

    private void addRegisterItem(String desc, int resourceId, String type) {
        View inflate = View.inflate(this, R.layout.shop_view_register_item, null);
        ImageView img = inflate.findViewById(R.id.add_customer_img);
        EditText editEdit = inflate.findViewById(R.id.add_customer_edit);
        img.setBackgroundResource(resourceId);
        editEdit.setHint(desc);
        inflate.setTag(desc);
        if (type.equals("string")) {
            editEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (type.equals("number")) {
            editEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        mShopRegisterContainer.addView(inflate);
    }


    @Override
    protected void initAction() {

    }

    private int getSelectGender() {
        if (mGenderMan.isSelected()) {
            return SELECT_MAN;
        }
        if (mGenderWoman.isSelected()) {
            return SELECT_WOMAN;
        }
        return SELECT_UN;
    }

    private String checkCustomer() {
        int childCount = mShopRegisterContainer.getChildCount();

        String error = "";
        if (mCustomerModel.userModel == null || StringUtil.emptyOrNull(mCustomerModel.userModel.userId)) {
            return "请录入会员照片";
        }
        UserModel userModel = mCustomerModel.userModel;
        CustomerModel customerModel = new CustomerModel();


        int selectGender = getSelectGender();
        if (selectGender == 0) {
            return "请选择会员性别";
        }
        customerModel.gender = selectGender == 1 ? "男" : "女";
        for (int i = 0; i < childCount; i++) {
            View childAt = mShopRegisterContainer.getChildAt(i);
            Object tag = childAt.getTag();
            EditText editView = childAt.findViewById(R.id.add_customer_edit);
            String s = editView.getText().toString();
            if (StringUtils.isEmpty(s)) {
                error = tag + "不能为空";
                break;
            }
            if ("会员名称".equals(tag)) {
                customerModel.name = s;
                continue;
            } else if ("电话号码".equals(tag)) {
                customerModel.mobile = s;
                if(!ValidateUtil.isMobileNO(s)){
                    error = "手机号码不正确";
                    break;
                }
                continue;
            } else if ("邮箱".equals(tag)) {
                customerModel.email = s;
                if(!ValidateUtil.isEmail(s)){
                    error = "邮箱不合法";
                    break;
                }
                continue;
            } else if ("地址".equals(tag)) {
                customerModel.address = s;
                continue;
            } else if ("年龄".equals(tag)) {
                customerModel.age = StringUtil.toInt(s);
                continue;
            }
        }
        if (StringUtils.isEmpty(error)) {
            mCustomerModel = customerModel;
            mCustomerModel.userModel = userModel;
        }
        return error;
    }

    @Override
    protected void initView() {
        mSubmit = (Button) findViewById(R.id.submit);
        mGenderMan = findViewById(R.id.gender_man);
        mGenderWoman = findViewById(R.id.gender_woman);
        mUserIconViewImg = (ImageView) findViewById(R.id.user_icon_view_img);
        mShopRegisterContainer = (LinearLayout) findViewById(R.id.add_customer_container);
    }

    private void sendSubmitService() {
        if (isSubmitting) {
            StockUtil.showToastOnMainThread(CustomerRegisterPage.this, "请不要重复点击注册");
            return;
        }
        isSubmitting = true;
        log.LogW("进行用户注册");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StockSender instance = StockSender.getInstance();
                    HashMap<String, String> map = new HashMap<>();
                    File file = new File(mCustomerModel.userModel.localImagePath);
                    String s = StockSender.getInstance().toUploadFile(file, map);
                    JSONObject jsonObject = JSON.parseObject(s);
                    String imgUrl = jsonObject.getString("data");
                    mCustomerModel.imgUrl = imgUrl;
                    CustomerModel customerModel = instance.sendAddRegisterService(mCustomerModel);
                    if (!StringUtil.emptyOrNull(customerModel.customerId)) {
                        StockUtil.showToastOnMainThread(getApplicationContext(), "注册成功");
                        //注册成功，跳转用户详情页
                        Intent intent = new Intent();
                        intent.setClass(CustomerRegisterPage.this, CustomerMainPage.class);
                        intent.putExtra(ShopConstants.CUSTOMERID, customerModel.customerId);
                        startActivity(intent);
                        finish();
                    } else {
                        //删掉注册失败的图片
                        deleteOldRegisterCustomerUser();
                        //注册失败
                        StockUtil.showToastOnMainThread(CustomerRegisterPage.this, "注册失败");

                    }
                }finally {
                    isSubmitting = false;
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            deleteOldRegisterCustomerUser();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void deleteOldRegisterCustomerUser() {
        if (StringUtil.emptyOrNull(mCustomerModel.name)) {
            return;
        }
        UserBean userBean = StockUtil.userModel2userBean(mCustomerModel.userModel);
        boolean b = FaceDetectManager.deleteByUserInfo(userBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int pixelFromDip = DeviceUtil.getPixelFromDip(this, 16);
        if (id == R.id.submit) {
            String error = checkCustomer();
            if (StringUtil.emptyOrNull(error)) {
                sendSubmitService();
            } else {
                StockUtil.showToastOnMainThread(getApplicationContext(), error);
            }

        } else if (id == R.id.user_icon_view_img) {
            Intent intent = new Intent();
            intent.setClass(this, YuvRegistActivity.class);
            startActivityForResult(intent, RegisterCode);

        } else if (id == R.id.back) {
            finish();
        } else if (id == R.id.gender_man) {
            mGenderMan.setSelected(true);
            mGenderWoman.setSelected(false);
            mGenderMan.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_man_select), 0, pixelFromDip, pixelFromDip);
            mGenderWoman.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_woman), 0, pixelFromDip, pixelFromDip);
        } else if (id == R.id.gender_woman) {
            mGenderWoman.setSelected(true);
            mGenderMan.setSelected(false);
            mGenderMan.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_man), 0, pixelFromDip, pixelFromDip);
            mGenderWoman.setCompoundDrawable(getResources().getDrawable(R.drawable.shop_register_woman_select), 0, pixelFromDip, pixelFromDip);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RegisterCode && data != null && data.getExtras() != null) {
            UserModel userModel = (UserModel) data.getExtras().getSerializable(ShopConstants.CUSTOMER_MODEL);
            //进行用户注册
            float[] floats = FaceDetectManager.queryFeatureById(userModel.userId);
            for (int i = 0; i < floats.length; i++) {
                userModel.features.add(floats[i]);
            }
            mCustomerModel.userModel = userModel;
            refreshView(mCustomerModel.userModel);
            //识别成功后删除注册数据
            FaceDetectManager.deleteByUserInfo(FaceDetectManager.queryByUserId(userModel.userId));
        }
    }

    private void refreshView(UserModel userModel) {
        if (StringUtil.emptyOrNull(userModel.userId)) {
            return;
        }
        String localImagePath = userModel.localImagePath;
        File chooseImage = new File(localImagePath);
        String newFilePath = chooseImage.getParentFile()+File.separator+imageFilePathTemp;
        File chooseImageTemp = new File(newFilePath);
        if(chooseImageTemp.exists())chooseImageTemp.delete();
        chooseImageTemp = new File(newFilePath);
        try {
            copyFileUsingFileStreams(chooseImage,chooseImageTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userModel.localImagePath = newFilePath;
        Glide.with(this).load(BitmapFactory.decodeFile(localImagePath)).into(mUserIconViewImg);
    }

    private void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.getInstance().logE(e.getMessage());
        }finally {
            input.close();
            output.close();
        }
    }

}
