package com.lxl.mobile.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.widget.StockTextView;
import com.lxl.shop.widget.StockTitleView;

/**
 * Created by yanglei on 2018/11/23.
 */

public class CustomerMainPage extends PageInitActivity implements View.OnClickListener {

    StockTitleView mTitle;

    TextView mCustomerId;
    ImageView mCustomerImg;
    TextView mCustomerName;

    StockTextView mCustomerInfo;
    StockTextView mCustomerRecord;

    CustomerModel mCustomerModel = new CustomerModel();

    Handler mHander = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_customer_main);
        initView();
        initAction();
        initData();
    }

    @Override
    protected void initData() {
        //以id为配置
        final String customerId = getIntent().getExtras().getString(ShopConstants.CUSTOMERID);

        final Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CustomerModel customerModel = StockSender.getInstance().sendSelectCustomerService(customerId,activity);
                mCustomerModel = customerModel;
                mHander.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView(mCustomerModel);
                    }
                });

            }
        }).start();
    }

    @Override
    protected void initListeners() {

    }

    private void refreshView(CustomerModel customerModel) {
        mCustomerId.setText(customerModel.customerId);
        mCustomerName.setText(customerModel.name);
        Glide.with(getBaseContext()).load(customerModel.imgUrl).into(mCustomerImg);
    }


    @Override
    protected void initAction() {
        mCustomerInfo.setOnClickListener(this);
        mCustomerRecord.setOnClickListener(this);
    }

    @Override
    protected void initView() {

        mTitle = (StockTitleView) findViewById(R.id.page_title);

        mCustomerId = (TextView) findViewById(R.id.customer_id);
        mCustomerImg = (ImageView) findViewById(R.id.customer_icon);
        mCustomerName = (TextView) findViewById(R.id.customer_name);

        mCustomerInfo = (StockTextView) findViewById(R.id.customer_info);
        mCustomerRecord = (StockTextView) findViewById(R.id.customer_record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.customer_info) {
            Intent intent = new Intent();
            intent.putExtra(ShopConstants.CUSTOMERID, mCustomerModel.customerId);
            intent.setClass(CustomerMainPage.this, CustomerInfoPage.class);
            startActivity(intent);
        } else if (id == R.id.customer_record) {
            Intent intent = new Intent();
            intent.putExtra(ShopConstants.CUSTOMERID, mCustomerModel.customerId);
            intent.setClass(CustomerMainPage.this, CustomerShoppingHistoryPage.class);
            startActivity(intent);
        }
    }

}
