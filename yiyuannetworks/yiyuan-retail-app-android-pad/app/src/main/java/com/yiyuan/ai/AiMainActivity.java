package com.yiyuan.ai;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aiwinn.base.activity.BaseActivity;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.frame.wangyu.retrofitframe.common.DownloadListener;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;
import com.yiyuan.ai.activity.GameListActivity;
import com.yiyuan.ai.activity.PhotoActivity;
import com.yiyuan.ai.activity.QueryMessageDialogActivity;
import com.yiyuan.ai.activity.ReWardActivity;
import com.yiyuan.ai.activity.RegisterActivity;
import com.yiyuan.ai.activity.RoBotActivity;
import com.yiyuan.ai.model.MainModel;
import com.yiyuan.ai.retrofit.model.VersionModel;
import com.yiyuan.ai.retrofit.response.VersionResponse;
import com.yiyuan.ai.util.AppUtil;
import com.yiyuan.ai.websocket.WsManager;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

import static com.yiyuan.ai.AIConstants.AI_PATH;
import static com.yiyuan.ai.AIConstants.AI_PATH_Folder;

/**
 * Created by wangyu on 2019/4/10.
 */

public class AiMainActivity extends BaseActivity {

    RecyclerView mRecyclerView;

    AiActivityAdapter aiActivityAdapter;

    Map<String,MainModel> mainModelMap = new HashMap<>();
    @Override
    public int getLayoutId() {
        return R.layout.ai_activity_main;
    }

    @Override
    public void initViews() {
        mRecyclerView = findViewById(R.id.ai_rlv);
    }

    @Override
    public void initData() {
//        updateVersion();//更新版本检查
        WsManager.getInstance(mContext).init();
        List<MainModel> mainModelList = initMainModelList();
        List<String> indexList = new ArrayList<>();
        for(int i=0;i<mainModelList.size();i++){
            indexList.add(i+"");
            mainModelMap.put(i+"",mainModelList.get(i));
        }
        GridLayoutManager lm = new GridLayoutManager(AiMainActivity.this, 3);
        mRecyclerView.setLayoutManager(lm);
        aiActivityAdapter = new AiActivityAdapter(indexList);
        mRecyclerView.setAdapter(aiActivityAdapter);
    }

    private void updateVersion() {
        VersionModel.getInstance().queryVersion(new Subscriber<VersionResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(VersionResponse versionResponse) {
                final String newApkName = "version_new.apk";
                if(versionResponse.code.equals("0") && versionResponse.data > 0){//需要更新
                    DownloadUtil.getInstance(AIConstants.mBaseUrl).downloadFileDefault(mContext,
                            "http://al.wtianyu.com:3010/filedownload?path=/version.apk", AI_PATH_Folder, newApkName, true, false, true, new DownloadListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onProgress(int i) {

                                }

                                @Override
                                public void onFinish(String s) {
                                    AppUtil.installApk(mContext,new File(AI_PATH+ File.separator+newApkName));
                                }

                                @Override
                                public void onFailure() {

                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onPause() {

                                }
                            });
                }
            }
        });
    }

    private List<MainModel> initMainModelList() {
        List<MainModel> mainModelList = new ArrayList<>();
        mainModelList.add(new MainModel(getResources().getString(R.string.ai_main_tab_register),R.drawable.user,"#55CFFF"));
        mainModelList.add(new MainModel(getResources().getString(R.string.ai_main_tab_game),R.drawable.game,"#7B87FF"));
        mainModelList.add(new MainModel(getResources().getString(R.string.ai_main_tab_photo),R.drawable.photo,"#F7A01D"));
        mainModelList.add(new MainModel(getResources().getString(R.string.ai_main_tab_message),R.drawable.message,"#90DA48"));
        mainModelList.add(new MainModel(getResources().getString(R.string.ai_main_tab_money),R.drawable.money,"#FC84A2"));
//        mainModelList.add(new MainModel("智能机器人",R.drawable.user,"#55CFFF"));
        return mainModelList;
    }


    class AiActivityAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {
        public AiActivityAdapter(List<String> indexList) {
            super(R.layout.item_ai_activity,indexList);
        }
        @Override
        protected void convert(BaseViewHolder helper, String item) {
            if (item != null) {
                MainModel mainModel = mainModelMap.get(item);
                helper.setBackgroundColor(R.id.item_ll, Color.parseColor(mainModel.getColor()));
                helper.setText(R.id.item_tv,mainModel.getName());
                helper.setImageResource(R.id.item_iv,mainModel.getResId());
            }
        }
    }

    @Override
    public void initListeners() {
        aiActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0://注册
                        mIntent.setClass(AiMainActivity.this, RegisterActivity.class);
                        startActivity(mIntent);
                        break;
                    case 1://游戏
//                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.imangi.templerun2");
//                        if (intent != null) {
//                            startActivity(intent);
//                        }
                        mIntent.setClass(AiMainActivity.this, GameListActivity.class);
                        startActivity(mIntent);
                        break;
                    case 2://拍照
                        mIntent.setClass(AiMainActivity.this, PhotoActivity.class);
                        startActivity(mIntent);
                        break;
                    case 3://信息查询
                        mIntent.setClass(AiMainActivity.this, QueryMessageDialogActivity.class);
                        startActivity(mIntent);
                        break;
                    case 4://红包
                        mIntent.setClass(AiMainActivity.this, ReWardActivity.class);
                        startActivity(mIntent);
                        break;
                    case 5://
                        mIntent.setClass(AiMainActivity.this, RoBotActivity.class);
                        startActivity(mIntent);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
//        DialogUtil.finishActivity(this);
        finish();
    }
}
