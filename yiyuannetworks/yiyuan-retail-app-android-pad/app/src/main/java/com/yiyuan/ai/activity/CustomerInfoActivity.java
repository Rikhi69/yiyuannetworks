package com.yiyuan.ai.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.ai.widget.CircleImageView;

/**
 * Created by wangyu on 2019/4/15.
 */

public class CustomerInfoActivity extends Activity {

    private LinearLayout llMain;
    private CustomerModel customerModel;

    private CircleImageView user_icon_view_img;
    private ImageView gender;
    private TextView name;
    private TextView phone;
    private TextView edit;
    private TextView exit;

    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_activity_customer_info);
        mContext = this;
        initView();
        initData();
        initLinstner();
    }

    private void initLinstner() {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PerfectMessageActivity.class);
                intent.putExtra("customerModel",customerModel);
                mContext.startActivity(intent);
                finish();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    private void initData() {
        Glide.with(getApplicationContext()).load(customerModel.imgUrl).into(user_icon_view_img);
        if(customerModel.gender.equals("男")){
            gender.setImageResource(R.drawable.shop_register_man_select);
        }else{
            gender.setImageResource(R.drawable.shop_register_woman_select);
        }
        if(!StringUtils.isEmpty(customerModel.mobile)){
            phone.setText(customerModel.mobile);
        }
        name.setText(customerModel.name);
    }

    private void initView() {
        customerModel = (CustomerModel) getIntent().getSerializableExtra("customerModel");
        if(customerModel == null){
            ToastUtils.showLong(R.string.info_data_error);
            finish();
        }
        llMain = findViewById(R.id.ll_main);
        llMain.setLayoutParams( getLayoutPara(llMain.getLayoutParams(), ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()*2/5));

        user_icon_view_img = findViewById(R.id.user_icon_view_img);
        gender = findViewById(R.id.gender);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);

        edit = findViewById(R.id.edit);
        exit = findViewById(R.id.exit);
    }

    private ViewGroup.LayoutParams getLayoutPara(ViewGroup.LayoutParams layoutParams, int w, int h) {
        if(w>0)
            layoutParams.width = (int) w;
        if(h>0)
            layoutParams.height = (int) h;
        return layoutParams;
    }
}
