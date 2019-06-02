package com.aiwinn.faceattendance.adapter;

import com.aiwinn.faceattendance.R;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

/**
 * com.aiwinn.faceattendance.adapter
 * SDK_ATT
 * 2018/08/25
 * Created by LeoLiu on User
 */

public class ActivityAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {

    public ActivityAdapter(ArrayList<String> strings) {
        super(R.layout.item_activity, strings);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (item != null) {
            helper.setText(R.id.activity,item);
        }
    }

}
