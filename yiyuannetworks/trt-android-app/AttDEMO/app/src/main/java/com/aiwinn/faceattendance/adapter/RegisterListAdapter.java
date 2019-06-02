package com.aiwinn.faceattendance.adapter;

import android.view.View;
import android.widget.ImageView;

import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.bean.RegisterListBean;
import com.aiwinn.faceattendance.ui.v.RegisterListView;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * com.aiwinn.faceattendance.adapter
 * SDK_ATT
 * 2018/08/25
 * Created by LeoLiu on User
 */

public class RegisterListAdapter extends BaseItemDraggableAdapter<RegisterListBean, BaseViewHolder> {

    RegisterListView mView ;

    public RegisterListAdapter(List<RegisterListBean> data , RegisterListView view) {
        super(R.layout.item_register, data);
        mView = view;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final RegisterListBean registerListBean) {
        if (registerListBean.isShow()) {
            helper.setGone(R.id.select,true);
            helper.setGone(R.id.delete,false);
            if (registerListBean.isChoose()) {
                Glide.with(helper.getConvertView()).load(R.drawable.ic_success).into((ImageView) helper.getView(R.id.select));
            }else {
                Glide.with(helper.getConvertView()).load(R.drawable.ic_error).into((ImageView) helper.getView(R.id.select));
            }
        }else {
            helper.setGone(R.id.select,false);
            helper.setGone(R.id.delete,true);
        }
        final UserBean item = registerListBean.getUserBean();
        if (item != null) {
            Glide.with(helper.getConvertView()).load(item.localImagePath).into((ImageView) helper.getView(R.id.img));
            helper.setText(R.id.name,item.name);
            helper.setText(R.id.rid,"ID : "+item.userId);
            helper.setOnClickListener(R.id.delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mView.deleteFace(helper.getPosition(),item);
                }
            });
        }
    }

}
