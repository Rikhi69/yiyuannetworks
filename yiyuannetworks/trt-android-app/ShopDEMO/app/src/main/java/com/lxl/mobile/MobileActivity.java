package com.lxl.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.NetListener;
import com.bumptech.glide.Glide;
import com.lxl.SettingActivity;
import com.lxl.mobile.page.CustomerRecordListPage;
import com.lxl.mobile.page.CustomerRegisterListPage;
import com.lxl.mobile.page.CustomerRegisterPage;
import com.lxl.shop.AttApp;
import com.lxl.shop.InitActivity;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.service.ListenerCustomerInService;
import com.lxl.shop.service.SyncCustomerServiceImpl;
import com.lxl.shop.utils.DailogUtil;
import com.lxl.shop.utils.LogUtil;

/**
 * Created by yanglei on 2018/11/23.
 */

public class MobileActivity extends InitActivity implements View.OnClickListener {

    TextView addCustomer;
    TextView detailCustomer;

    SyncCustomerServiceImpl syncService;

    SharedPreferences SP_Authorization = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_main);
        initView();
        initAction();
        initData();
        initListeners();
    }

    @Override
    protected void initData() {
        //配置文件数据初始化
        StockSender.getInstance();
        //启动websocket监听
        new Thread(new Runnable() {
            @Override
            public void run() {
                WsManager.getInstance(getApplicationContext()).init();
            }
        }).start();
    }

    @Override
    protected void initListeners() {
        addCustomer.setOnClickListener(this);
        detailCustomer.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.record_customer).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
    }

    @Override
    protected void initAction() {
        SP_Authorization = getSharedPreferences(ShopConstants.SP_Authorization, 0);
        if(!SP_Authorization.getBoolean("mAuthorizationTag",false)) {
            callAuthorization();
        }
    }

    @Override
    protected void initView() {
        addCustomer = findViewById(R.id.add_customer);
        detailCustomer = findViewById(R.id.detail_customer);
    }

    @Override
    protected void onDestroy() {
        //关闭websocket监听
        WsManager.getInstance(getApplicationContext()).disconnect();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_customer) {
            Intent intent = new Intent();
            intent.setClass(this, CustomerRegisterPage.class);
            startActivity(intent);
        } else if (id == R.id.detail_customer) {
            Intent intent = new Intent();
            intent.setClass(this, CustomerRegisterListPage.class);
            startActivity(intent);

        } else if (id == R.id.record_customer) {
            Intent intent = new Intent();
            intent.setClass(this, CustomerRecordListPage.class);
            startActivity(intent);
        } else if (id == R.id.test) {
            Intent intent = new Intent();
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.back) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void callAuthorization(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.networkAuthorization(new NetListener() {
                    @Override
                    public void onComplete() {
                        updataUI(false,"");
                        AttApp.initSDK();
                    }

                    @Override
                    public void onError(Status status, final String s) {
                        updataUI(true,s);
                    }
                });
            }
        }).start();
    }
    void updataUI(final boolean tag,String s){
        if (tag) {
            SP_Authorization.edit().putBoolean("mAuthorizationTag",false).apply();
            LogUtil.getInstance().logE("授权:"+s);
        }else {
            SP_Authorization.edit().putBoolean("mAuthorizationTag",true).apply();
            LogUtil.getInstance().logE("授权成功");
        }
    }
}
