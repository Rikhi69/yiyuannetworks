package com.aiwinn.faceattendance.ui.m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.base.widget.AttPopwindow;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.adapter.RegisterListAdapter;
import com.aiwinn.faceattendance.bean.RegisterListBean;
import com.aiwinn.faceattendance.ui.p.RegisterListPresenter;
import com.aiwinn.faceattendance.ui.p.RegisterListPresenterImpl;
import com.aiwinn.faceattendance.ui.p.YuvRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.v.RegisterListView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.m
 * SnapShot
 * 2018/08/28
 * Created by LeoLiu on User
 */

public class RegisterListActivity extends BaseActivity implements View.OnClickListener ,RegisterListView {

    ImageView mBack;
    TextView mTitle;
    TextView mRight;
    RecyclerView mRecyclerView;
    RegisterListPresenter mPresenter;
    RegisterListAdapter mAdapter;
    private AttPopwindow mPopupWindow;
    private EditText mPopWindowIdEditText;
    private EditText mPopWindowNameEditText;
    private ImageView mPopWindowImageView;
    private TextView mCancelTextView;
    private TextView mSureTextView;
    private RelativeLayout mSelect;
    private TextView mSelectMsg;
    private TextView mSelectAll;
    private TextView mSelectPage;
    boolean select = false;
    List<UserBean> mUserList = new ArrayList<>();
    List<UserBean> mAllUserList = new ArrayList<>();
    List<UserBean> mSelectUserList = new ArrayList<>();
    List<RegisterListBean> mRegisterListBeans = new ArrayList<>();
    private boolean mSelectAllDelete;

    @Override
    public int getLayoutId() {
        return R.layout.activity_registerlist;
    }

    @Override
    public void initViews() {
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mRight = findViewById(R.id.right);
        mRecyclerView = findViewById(R.id.rv);
        mSelect = findViewById(R.id.selectrl);
        mSelectMsg = findViewById(R.id.selectmsg);
        mSelectAll = findViewById(R.id.selectall);
        mSelectPage = findViewById(R.id.selectpage);
    }

    @Override
    public void initData() {
        select = false;
        mUserList.clear();
        mAllUserList.clear();
        mSelectUserList.clear();
        mRegisterListBeans.clear();
        mPresenter = new RegisterListPresenterImpl(this);
        mTitle.setText(getResources().getString(R.string.list));
        mRight.setText(getResources().getString(R.string.selectmore));
        LinearLayoutManager detectRvManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(detectRvManager);
        mAdapter = new RegisterListAdapter(mRegisterListBeans,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (select) {
                    RegisterListBean registerListBean = mRegisterListBeans.get(position);
                    if (registerListBean.isChoose()) {
                        mRegisterListBeans.get(position).setChoose(false);
                        mSelectUserList.remove(registerListBean.getUserBean());
                    }else {
                        mRegisterListBeans.get(position).setChoose(true);
                        mSelectUserList.add(mUserList.get(position));
                    }
                    updateSelectNum();
                    mAdapter.notifyItemChanged(position,registerListBean);
                }else {
                    UserBean userBean = mUserList.get(position);
                    showPopWindow(position,userBean);
                }
            }
        });
        mSelectPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelectPage;
                if (mSelectUserList.size() == mRegisterListBeans.size()) {
                    isSelectPage = true;
                }else {
                    isSelectPage = false;
                }
                if (isSelectPage) {
                    for (int i = 0; i < mRegisterListBeans.size(); i++) {
                        RegisterListBean registerListBean = mRegisterListBeans.get(i);
                        registerListBean.setChoose(false);
                        registerListBean.setShow(true);
                        mAdapter.notifyItemChanged(i,registerListBean);
                    }
                    mSelectUserList.clear();
                    updateSelectNum();
                }else {
                    for (int i = 0; i < mRegisterListBeans.size(); i++) {
                        RegisterListBean registerListBean = mRegisterListBeans.get(i);
                        registerListBean.setChoose(true);
                        registerListBean.setShow(true);
                        mAdapter.notifyItemChanged(i,registerListBean);
                    }
                    mSelectUserList.clear();
                    mSelectUserList.addAll(mUserList);
                    updateSelectNum();
                }
            }
        });
        mSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectAllDelete) {
                    mSelectAllDelete = false;
                }else {
                    mSelectAllDelete = true;
                }
                if (mSelectAllDelete) {
                    for (int i = 0; i < mRegisterListBeans.size(); i++) {
                        RegisterListBean registerListBean = mRegisterListBeans.get(i);
                        registerListBean.setChoose(true);
                        registerListBean.setShow(true);
                        mAdapter.notifyItemChanged(i,registerListBean);
                    }
                    mSelectUserList.addAll(mAllUserList);
                    updateSelectNum();
                }else {
                    for (int i = 0; i < mRegisterListBeans.size(); i++) {
                        RegisterListBean registerListBean = mRegisterListBeans.get(i);
                        registerListBean.setChoose(false);
                        registerListBean.setShow(true);
                        mAdapter.notifyItemChanged(i,registerListBean);
                    }
                    mSelectUserList.clear();
                    updateSelectNum();
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        },mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetList();
    }

    int page = 1;
    void loadMore(){
        showDialog(getResources().getString(R.string.load));
        mPresenter.loadMore(page, new RegisterListPresenterImpl.loadMoreListener() {
            @Override
            public void loadMoreComplete(final List<UserBean> userBeans) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        mUserList.addAll(userBeans);
                        List<RegisterListBean> registerListBeans = new ArrayList<>();
                        registerListBeans.clear();
                        for (UserBean userBean : userBeans) {
                            RegisterListBean registerListBean = new RegisterListBean(userBean,mSelectAllDelete,select);
                            registerListBeans.add(registerListBean);
                        }
                        mAdapter.addData(registerListBeans);
                        mAdapter.loadMoreComplete();
                        showDialog(getResources().getString(R.string.load_done));
                        dissmisDialog();
                    }
                });
            }

            @Override
            public void loadMoreEnd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.loadMoreEnd();
                        showDialog(getResources().getString(R.string.load_done));
                        dissmisDialog();
                    }
                });
            }

            @Override
            public void loadAll(List<UserBean> mBeanList) {
                mAllUserList.addAll(mBeanList);
            }
        });
    }

    void resetList(){
        page = 1;
        mUserList.clear();
        mRegisterListBeans.clear();
        mAdapter.replaceData(mRegisterListBeans);
        loadMore();
    }

    boolean isShopPop = false;
    private void showPopWindow(final int position,final UserBean userBean) {
        if (isShopPop) {
            return;
        }
        isShopPop = true;
        View contentView  = LayoutInflater.from(this).inflate(R.layout.register_popwindow, null);
        mPopupWindow = new AttPopwindow.PopupWindowBuilder(RegisterListActivity.this)
                .setView(contentView )
                .enableOutsideTouchableDissmiss(false)
                .create()
                .showAtLocation(RegisterListActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopWindowIdEditText = contentView.findViewById(R.id.et_register_id);
        mPopWindowNameEditText = contentView.findViewById(R.id.et_register_name);
        mPopWindowImageView = contentView.findViewById(R.id.iv_register_pop);
        mCancelTextView = contentView.findViewById(R.id.btn_register_cancel);
        mSureTextView = contentView.findViewById(R.id.btn_register_sure);
        Glide.with(this).load(userBean.localImagePath).into(mPopWindowImageView);
        mPopWindowIdEditText.setText(userBean.userId);
        mPopWindowNameEditText.setText(userBean.name);
        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dissmiss();
            }
        });
        mSureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mPopWindowNameEditText.getText().toString().trim();
                String id = mPopWindowIdEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id)) {
                    ToastUtils.showShort(getResources().getString(R.string.name_empty));
                } else {
                    userBean.name = name;
                    userBean.userId = id;
                    if (FaceDetectManager.updateUser(userBean)) {
                        mAdapter.notifyItemChanged(position,userBean);
                        ToastUtils.showShort(getResources().getString(R.string.update_success));
                    }else {
                        ToastUtils.showShort(getResources().getString(R.string.update_fail));
                    }
                    mPopupWindow.dissmiss();
                }
            }
        });
        mPopupWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogUtils.d(YuvRegistPresenterImpl.HEAD,"dismiss");
                isShopPop = false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back:
            case R.id.title:
                RegisterListActivity.this.finish();
                break;

            case R.id.right:
                if (mUserList.size() == 0) {
                    return;
                }
                if (select) {
                    if (mSelectUserList.size() == 0) {
                        resetSelect();
                        for (RegisterListBean registerListBean : mRegisterListBeans) {
                            registerListBean.setShow(false);
                            registerListBean.setChoose(false);
                        }
                        mAdapter.replaceData(mRegisterListBeans);
                        return;
                    }
                    if (mUserList.size() == mSelectUserList.size()) {
                        deleteAllFace(true);
                    }else {
                        deleteAllFace(false);
                    }
                }else {
                    setSelect();
                    mSelectUserList.clear();
                    mSelectMsg.setText(getResources().getString(R.string.select)+0);
                    for (int i = 0; i < mRegisterListBeans.size(); i++) {
                        RegisterListBean registerListBean = mRegisterListBeans.get(i);
                        registerListBean.setShow(true);
                        registerListBean.setChoose(false);
                        mAdapter.notifyItemChanged(i,registerListBean);
                    }
                }
                break;
        }
    }

    @Override
    public void deleteFace(final int index, final UserBean face) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterListActivity.this);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(getResources().getString(R.string.delete));
        builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                mPresenter.deleteFace(index,face);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builder.show();
    }

    public void deleteAllFace(final boolean tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterListActivity.this);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(getResources().getString(R.string.delete));
        builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                if (tag) {
                    mPresenter.deleteAll();
                }else {
                    mPresenter.delete(mSelectUserList);
                }
                showDialog(getResources().getString(R.string.deleting));
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builder.show();
    }

    @Override
    public void deleteDone(final int index,final UserBean face) {
        if (face != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAllUserList.remove(face);
                    mUserList.remove(index);
                    mAdapter.remove(index);
                }
            });
        }
    }

    @Override
    public void deleteFail(final UserBean face) {
        if (face != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showLong(getResources().getString(R.string.delete_fail)+" : "+face.name);
                }
            });
        }
    }

    @Override
    public void deleteAllDone(final boolean result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dissmisDialog();
                resetSelect();
                if (result) {
                    resetList();
                }else {
                    ToastUtils.showLong(getResources().getString(R.string.delete_fail));
                }
            }
        });
    }

    void resetSelect(){
        select = false;
        mSelect.setVisibility(View.GONE);
        mRight.setText(getResources().getString(R.string.selectmore));
    }

    void setSelect(){
        select = true;
        mSelect.setVisibility(View.VISIBLE);
        mRight.setText(getResources().getString(R.string.delete));
    }

    void updateSelectNum(){
        mSelectMsg.setText(getResources().getString(R.string.select)+" "+mSelectUserList.size());
    }

}
