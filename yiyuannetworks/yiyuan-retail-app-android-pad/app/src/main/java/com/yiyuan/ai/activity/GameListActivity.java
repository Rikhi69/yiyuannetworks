package com.yiyuan.ai.activity;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aiwinn.base.activity.BaseActivity;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.frame.wangyu.retrofitframe.util.ToastUtil;
import com.yiyuan.ai.R;
import com.yiyuan.ai.common.DialogUtil;
import com.yiyuan.ai.model.MainModel;
import com.yiyuan.ai.websocket.WsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyu on 2019/4/10.
 */

public class GameListActivity extends BaseActivity {

    RecyclerView mRecyclerView;

    AiActivityAdapter aiActivityAdapter;

    private ImageView tvShowYe;

    Map<String,MainModel> mainModelMap = new HashMap<>();
    @Override
    public int getLayoutId() {
        return R.layout.ai_activity_game_list;
    }

    @Override
    public void initViews() {
        mRecyclerView = findViewById(R.id.ai_rlv);
        tvShowYe = findViewById(R.id.shouye);
    }

    @Override
    public void initData() {
        WsManager.getInstance(mContext).init();
        List<MainModel> mainModelList = initMainModelList();
        List<String> indexList = new ArrayList<>();
        for(int i=0;i<mainModelList.size();i++){
            indexList.add(i+"");
            mainModelMap.put(i+"",mainModelList.get(i));
        }
        GridLayoutManager lm = new GridLayoutManager(GameListActivity.this, 3);
        mRecyclerView.setLayoutManager(lm);
        aiActivityAdapter = new AiActivityAdapter(indexList);
        mRecyclerView.setAdapter(aiActivityAdapter);
    }

    private List<MainModel> initMainModelList() {
        List<MainModel> mainModelList = new ArrayList<>();
        mainModelList.add(new MainModel("弹球大作战",R.drawable.game,"#55CFFF"));
        mainModelList.add(new MainModel("大鱼吃小鱼",R.drawable.game,"#7B87FF"));
        mainModelList.add(new MainModel("游戏3",R.drawable.game,"#F7A01D"));
        mainModelList.add(new MainModel("游戏4",R.drawable.game,"#90DA48"));
        mainModelList.add(new MainModel("游戏5",R.drawable.game,"#FC84A2"));
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
                    case 0:
                        mIntent.setClass(GameListActivity.this, GameMainActivity.class);
                        mIntent.putExtra("url","http://sxiao.4399.com/4399swf/upload_swf/ftp24/gamehwq/20180419/15/index.html");
                        startActivity(mIntent);
                        break;
                    case 1:
                        mIntent.setClass(GameListActivity.this, GameMainActivity.class);
                        mIntent.putExtra("url","http://sda.4399.com/4399swf/upload_swf/ftp26/csya/20181017/4/gameIndex.html");
                        startActivity(mIntent);
                        break;
                    case 2://拍照
                        ToastUtil.showMessage("游戏3");
                        break;
                    case 3://信息查询
                        ToastUtil.showMessage("游戏4");
                        break;
                    case 4://红包
                        ToastUtil.showMessage("游戏5");
                        break;
                }
            }
        });
        tvShowYe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.finishActivity(mContext);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        DialogUtil.finishActivity(this);
        finish();
    }
}
