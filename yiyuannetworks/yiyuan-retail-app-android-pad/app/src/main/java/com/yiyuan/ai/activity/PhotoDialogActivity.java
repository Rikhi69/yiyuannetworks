package com.yiyuan.ai.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aiwinn.base.util.ScreenUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.common.FileUtil;
import com.yiyuan.ai.common.HttpUtil;
import com.yiyuan.ai.common.ValidateUtil;
import com.yiyuan.ai.model.CustomerModel;
import com.yiyuan.ai.widget.CircleImageView;

import java.io.File;
import java.util.HashMap;

/**
 * Created by wangyu on 2019/4/16.
 */

public class PhotoDialogActivity extends Activity {

    private LinearLayout ll_view_photo;

    private Context mContext;

    private ImageView iv_photo_view;

    private TextView photo_again;

    private TextView send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_activity_photo_view);
        mContext = this;
        initView();
        initData();
        initListeners();
    }

    private void initListeners() {
        iv_photo_view.setImageBitmap(PhotoActivity.dataImageBitmap);
        photo_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.editDialog(mContext, getString(R.string.photo_dialog_email_input), new DialogUtil.EditDialogText() {
                    @Override
                    public void getText(final String text) {
                        //按下确定键后的事件
                        if(StringUtils.isEmpty(text)||!text.contains("@")){
                            ToastUtils.showLong(getString(R.string.photo_dialog_email_error_type));
                            return;
                        }
                        DialogUtil.showDialog(mContext,getString(R.string.photo_dialog_email_sending));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String s1 = "";
                                try {
                                    File file = FileUtil.compressImage(PhotoActivity.dataImageBitmap);
                                    String imageUrl = HttpUtil.getInstance().toUploadFile(file, new HashMap<String, String>());
                                    s1 = HttpUtil.getInstance().sendEmail(((JSONObject) JSONObject.parse(imageUrl)).getString("data"),text);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    s1 = "error";
                                }finally {
                                    if("error".equals(s1)){
                                        ToastUtils.showLong(getString(R.string.photo_dialog_email_error));
                                    }else{
                                        ToastUtils.showLong(getString(R.string.photo_dialog_email_success));
                                        finish();
                                    }
                                    DialogUtil.dissmisDialog();
                                }
                            }
                        }).start();
                    }
                });
            }
        });
    }

    private void initData() {
    }

    private void initView() {
        iv_photo_view = findViewById(R.id.iv_photo_view);
        photo_again = findViewById(R.id.photo_again);
        send = findViewById(R.id.send);
        ll_view_photo = findViewById(R.id.ll_view_photo);
        ll_view_photo.setLayoutParams( getLayoutPara(ll_view_photo.getLayoutParams(), ScreenUtils.getScreenWidth()/2,ScreenUtils.getScreenHeight()*2/5));
    }

    private ViewGroup.LayoutParams getLayoutPara(ViewGroup.LayoutParams layoutParams, int w, int h) {
        if(w>0)
            layoutParams.width = (int) w;
        if(h>0)
            layoutParams.height = (int) h;
        return layoutParams;
    }
}
