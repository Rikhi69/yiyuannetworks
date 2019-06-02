package com.aiwinn.gaa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * com.aiwinn.facelock
 * 2017/11/28
 * Created by LeoLiu on User.
 */

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView mBack;
    TextView mTitle;
    private EditText mCamera_id;
    private EditText mCamera_rotate;
    private EditText mCamera_w;
    private EditText mCamera_h;
    private TextView mSetting;
    private int mCId;
    private int mCRo;
    private int mCW;
    private int mCH;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mCamera_id = (EditText) findViewById(R.id.camera_id);
        mCamera_rotate = (EditText) findViewById(R.id.camera_rotate);
        mCamera_w = (EditText) findViewById(R.id.camera_w);
        mCamera_h = (EditText) findViewById(R.id.camera_h);
        mSetting = (TextView) findViewById(R.id.setting);
        mTitle.setText("自定义参数");
        mCamera_id.setText(AppConfig.CAMERA_ID+"");
        mCamera_rotate.setText(AppConfig.CAMERA_ROT+"");
        mCamera_w.setText(AppConfig.CAMERA_W+"");
        mCamera_h.setText(AppConfig.CAMERA_H+"");
        initListener();
    }

    private void initListener() {
        mBack.setOnClickListener(this);
        mSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.setting:
                String cid = mCamera_id.getEditableText().toString();
                String cro = mCamera_rotate.getEditableText().toString();
                String cw = mCamera_w.getEditableText().toString();
                String ch = mCamera_h.getEditableText().toString();
                if (cid.equals("") || cro.equals("") || cw.equals("") || ch.equals("")) {
                    showToast("参数为空");
                    return;
                }
                try {
                    mCId = Integer.parseInt(cid);
                    mCRo = Integer.parseInt(cro);
                    mCW = Integer.parseInt(cw);
                    mCH = Integer.parseInt(ch);
                } catch (Exception e) {
                    showToast("解析参数异常，请检查输入是否规范");
                    return;
                }
                setConfig();
                break;

            case R.id.back:
                finish();
                break;

        }
    }

    private void setConfig() {
        AppConfig.CAMERA_ID = mCId;
        AppConfig.CAMERA_ROT = mCRo;
        AppConfig.CAMERA_W = mCW;
        AppConfig.CAMERA_H = mCH;
        showToast("设置成功");
        finish();
    }

    private void showToast(String s) {
        Toast.makeText(ConfigActivity.this, s, Toast.LENGTH_SHORT).show();
    }

}
