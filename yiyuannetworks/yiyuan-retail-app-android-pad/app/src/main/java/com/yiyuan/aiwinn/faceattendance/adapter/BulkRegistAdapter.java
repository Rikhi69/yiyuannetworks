package com.yiyuan.aiwinn.faceattendance.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yiyuan.ai.R;
import com.yiyuan.aiwinn.faceattendance.bean.BulkRegistBean;
import com.yiyuan.aiwinn.faceattendance.ui.m.BulkRegistActivity;
import com.yiyuan.aiwinn.faceattendance.ui.p.BulkRegistPresenterImpl;
import com.yiyuan.aiwinn.faceattendance.ui.v.BulkRegistView;

import java.util.List;

/**
 * com.aiwinn.faceattendance.adapter
 * SDK_ATT
 * 2018/08/25
 * Created by LeoLiu on User
 */

public class BulkRegistAdapter extends BaseItemDraggableAdapter<BulkRegistBean, BaseViewHolder> {

    BulkRegistView mView ;

    public BulkRegistAdapter(List<BulkRegistBean> data , BulkRegistView view) {
        super(R.layout.item_bulkregist, data);
        mView = view;
    }

    @Override
    protected void convert(BaseViewHolder helper, final BulkRegistBean item) {
        if (item != null) {
            Glide.with(helper.getConvertView()).load(item.getFile()).into((ImageView) helper.getView(R.id.img));
            helper.setText(R.id.name, BulkRegistPresenterImpl.getName(item.getFile().getName()));
            if (item.isRegist()) {
                helper.setText(R.id.type,((BulkRegistActivity)mView).getString(R.string.registered));
                Glide.with(helper.getConvertView()).load(R.drawable.ic_success).into((ImageView) helper.getView(R.id.typeimg));
            }else {
                helper.setText(R.id.type,((BulkRegistActivity)mView).getString(R.string.unregistered));
                Glide.with(helper.getConvertView()).load(R.drawable.ic_error).into((ImageView) helper.getView(R.id.typeimg));
            }
        }
    }

}
