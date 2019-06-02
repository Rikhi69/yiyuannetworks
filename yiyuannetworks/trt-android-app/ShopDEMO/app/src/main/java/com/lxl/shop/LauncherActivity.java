package com.lxl.shop;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;


import com.lxl.shop.pag.MainActivity;
import com.lxl.mobile.MobileActivity;
import com.lxl.shop.common.AttConstants;
import com.lxl.shop.common.ShopConfig;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.utils.IOHelper;
import com.lxl.shop.utils.StockUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by yanglei on 2018/12/1.
 */

public class LauncherActivity extends InitActivity {

    boolean isFirst;
    ImageView img;
    boolean isMobile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        isFirst = true;

        setContentView(R.layout.shop_page_route);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsGranted) {
            if (isFirst) {
                initConfig();
                initRoute();
                isFirst = false;
            }
        } else {
            StockUtil.showToastOnMainThread(this, "请授予权限。");
        }
    }

    @Override
    protected void initView() {
        img = findViewById(R.id.route_image);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        if (StockUtil.isMoblie(this)) {
            img.setBackgroundResource(R.drawable.shop_route_bg_mobile_new);
//            img.setBackgroundResource(R.drawable.trt_start);
        } else {
            img.setBackgroundResource(R.drawable.shop_route_bg_pad_new);
        }
    }

    private void initConfig() {
        //判断进入到哪里
        File file = new File(ShopConstants.CONFIG_PATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        File tempfile = new File(ShopConstants.TEMP_PATH);
        if(!tempfile.exists()){
            tempfile.mkdirs();
        }

        String resultValue = "";
        String count = "";
        String support = "";
        if (file.exists()) {
            //读取配置文件
            InputStream inputStream = IOHelper.fromFileToIputStream(file);//权限问题
            List<String> strings = IOHelper.readListStrByCode(inputStream, "utf-8");
            for (String str : strings) {
                if (StringUtil.emptyOrNull(str) || !str.contains("=")) {
                    continue;
                }
                String[] split = str.split("=");
                String key = split[0];
                String value = split[1];
                if ("url".equals(key)) {
                } else if ("first".equals(key)) {
                    resultValue = value;
                }else if ("count".equals(key)) {
                    count = value;
                }else if ("support".equals(key)) {
                    support = value;
                }
            }
        } else {
            //使用配置文件
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("url="+ StockSender.mBaseUrl+"/");
                builder.append("\n");
                builder.append("first=mobile");
                builder.append("\n");
                builder.append("count=1");
                builder.append("\n");
                builder.append("support=camera,sdcard,video");//
                file.createNewFile();
                IOHelper.writerStrByCodeToFile(file, "utf-8", false, builder.toString());
                resultValue = "mobile";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ("mobile".equals(resultValue)) {
            isMobile = true;

            AttConstants.CAMERA_DEGREE = 90;
            AttConstants.PREVIEW_DEGREE = 90;
        } else {
            isMobile = false;
            //旋转角度
            AttConstants.CAMERA_DEGREE = 0;
            AttConstants.PREVIEW_DEGREE = 0;
        }
        ShopConfig.isPhone = isMobile;
        ShopConfig.count = count;
        if(StringUtil.emptyOrNull(support)){
            support="";
            ShopConfig.isSupportCamera = true;
        }
        if(support.toLowerCase().contains("camera")){
            ShopConfig.isSupportCamera = true;
        }else{
            ShopConfig.isSupportCamera = false;
        }
        if(support.toLowerCase().contains("sdcard")){
            ShopConfig.isSupportSDCard = true;
        }else{
            ShopConfig.isSupportSDCard = false;
        }
        if(support.toLowerCase().contains("video")){
            ShopConfig.isSupportVideo = true;
        }else{
            ShopConfig.isSupportVideo = false;
        }

        log.LogW("totalMemery:"+StockUtil.getTotalMemory(this));
        log.LogW("avaiMemery:"+StockUtil.getAvaiMemory(this));

    }

    private void initRoute() {
        final Intent intent = new Intent();
        intent.setClass(this, MobileActivity.class);
//        if (isMobile) {
//            intent.setClass(this, MobileActivity.class);
//        } else {
//            intent.setClass(this, MainActivity.class);
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void initListeners() {

    }


}
