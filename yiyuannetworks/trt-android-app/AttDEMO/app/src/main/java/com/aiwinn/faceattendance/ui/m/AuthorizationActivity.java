package com.aiwinn.faceattendance.ui.m;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.faceattendance.AttApp;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.NetListener;
import com.bumptech.glide.Glide;

/**
 * com.aiwinn.faceattendance.ui.m
 * SDK_ATT
 * 2018/09/06
 * Created by LeoLiu on User
 */

public class AuthorizationActivity extends BaseActivity implements View.OnClickListener{

    TextView mTitle;
    ImageView mBack;

    TextView mAuthorization;
    TextView mResultTv;
    ImageView mResultIm;
    private boolean mAuthorizationTag;

    @Override
    public int getLayoutId() {
        return R.layout.activity_authorization;
    }

    @Override
    public void initViews() {
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mAuthorization = findViewById(R.id.authorization);
        mResultTv = findViewById(R.id.result_tv);
        mResultIm = findViewById(R.id.result_im);
    }

    @Override
    public void initData() {
        mAuthorizationTag = false;
        mTitle.setText(getResources().getString(R.string.authorization));
        if (!AttConstants.INIT_STATE && AttConstants.INIT_STATE_ERROR == Status.AuthorizationVerifyFail) {
            updataUI(true);
        }else {
            updataUI(false);
        }
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mAuthorization.setOnClickListener(this);
    }

    void updataUI(boolean tag){
        if (tag) {
            mAuthorizationTag = false;
            mResultTv.setText(getResources().getString(R.string.authorization_fail));
            Glide.with(this).load(R.drawable.ic_error).into(mResultIm);
        }else {
            mAuthorizationTag = true;
            mResultTv.setText(getResources().getString(R.string.authorization_success));
            Glide.with(this).load(R.drawable.ic_success).into(mResultIm);
        }
    }

    void callAuthorization(){
        showDialog(getResources().getString(R.string.authorization));
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceDetectManager.networkAuthorization(new NetListener() {
                    @Override
                    public void onComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dissmisDialog();
                                updataUI(false);
                                AttApp.initSDK();
                            }
                        });
                    }

                    @Override
                    public void onError(Status status, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dissmisDialog();
                                ToastUtils.showLong(s);
                                updataUI(true);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.title:
            case R.id.back:
                finish();
                break;

            case R.id.authorization:
                if (mAuthorizationTag) {
                    ToastUtils.showLong(getResources().getString(R.string.authorization_had));
                }else {
                    callAuthorization();
                }
                break;

        }
    }
}
