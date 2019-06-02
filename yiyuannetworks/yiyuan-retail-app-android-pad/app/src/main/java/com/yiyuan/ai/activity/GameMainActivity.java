package com.yiyuan.ai.activity;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.aiwinn.base.activity.BaseActivity;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.constant.RetrofitConfig;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;

/**
 * Created by wangyu on 2019/4/16.
 */

public class GameMainActivity extends BaseActivity {

    private WebView webView;
    private String gameUrl;
    @Override
    public int getLayoutId() {
        return R.layout.ai_activity_game;
    }

    @Override
    public void initViews() {
        webView = findViewById(R.id.webView1);
    }

    @Override
    public void initData() {
        gameUrl = getIntent().getStringExtra("url");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true); //设置可以支持缩放
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.loadUrl(gameUrl);
    }

    @Override
    public void initListeners() {
    }

    @Override
    public void onBackPressed() {
        DialogUtil.finishGameActivity(this);
    }
}
