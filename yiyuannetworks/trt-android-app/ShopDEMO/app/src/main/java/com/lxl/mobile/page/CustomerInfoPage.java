package com.lxl.mobile.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.widget.StockInfoBar;

/**
 * Created by yanglei on 2018/11/24.
 */

public class CustomerInfoPage extends PageInitActivity {
    ImageView mCustomerIcon;
    LinearLayout mCustomerOtherInfo;
    LinearLayout mCustomerBaseInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_customer_info);
        initView();
        initAction();
        initData();
    }

    @Override
    protected void initView() {
        mCustomerIcon = findViewById(R.id.customer_icon);
        mCustomerBaseInfo = findViewById(R.id.customer_base_info);
        mCustomerOtherInfo = findViewById(R.id.customer_other_info);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        final String customerId = getIntent().getExtras().getString(ShopConstants.CUSTOMERID);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CustomerModel customerModel = StockSender.getInstance().sendSelectCustomerService(customerId);
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView(customerModel);
                    }
                });

            }
        }).start();
    }

    @Override
    protected void initListeners() {

    }

    private void refreshView(CustomerModel customerModel) {
        StockInfoBar bar1 = new StockInfoBar(this);
        StockInfoBar bar2 = new StockInfoBar(this);
        StockInfoBar bar3 = new StockInfoBar(this);
        StockInfoBar bar4 = new StockInfoBar(this);
        StockInfoBar bar5 = new StockInfoBar(this);
        StockInfoBar bar6 = new StockInfoBar(this);

        bar1.bindInfoData("会员名", customerModel.name);
        bar2.bindInfoData("性别", customerModel.gender);
        bar3.bindInfoData("年龄", String.valueOf(customerModel.age));
        mCustomerBaseInfo.addView(bar1);
        mCustomerBaseInfo.addView(bar2);
        mCustomerBaseInfo.addView(bar3);

        bar4.bindInfoData("手机号", customerModel.mobile);
        bar5.bindInfoData("邮箱", customerModel.email);
        bar6.bindInfoData("地址", customerModel.address);
        mCustomerOtherInfo.addView(bar4);
        mCustomerOtherInfo.addView(bar5);
        mCustomerOtherInfo.addView(bar6);

        Glide.with(getBaseContext()).load(customerModel.imgUrl).into(mCustomerIcon);
    }

}
