package com.aiwinn.faceattendance.ui.m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.adapter.BulkRegistAdapter;
import com.aiwinn.faceattendance.bean.BulkRegistBean;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.faceattendance.ui.p.BulkRegistPresenter;
import com.aiwinn.faceattendance.ui.p.BulkRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.v.BulkRegistView;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.m
 * SnapShot
 * 2018/09/03
 * Created by LeoLiu on User
 */

public class BulkRegistActivity extends BaseActivity implements View.OnClickListener , BulkRegistView {

    TextView mTitle;
    ImageView mBack;
    TextView mMsg;
    TextView mBegin;
    RecyclerView mRv;
    private BulkRegistPresenter mPresenter;
    private BulkHandler mHandler;

    public static final int MSG_FILE_EMPTY = 1;
    public static final int MSG_FILE_LIST = 2;
    public static final int MSG_FILE_LIST_UPDATE = 3;
    public static final int MSG_UPDATE_DIALOG = 4;
    public static final int MSG_DISMISS_DIALOG = 5;
    public static final int MSG_REFRESH = 6;
    private List<BulkRegistBean> mBeanList;
    private List<File> mFileList;
    private BulkRegistAdapter mBulkAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bulkregist;
    }

    @Override
    public void initViews() {
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mMsg = findViewById(R.id.msg);
        mRv = findViewById(R.id.rv);
        mBegin = findViewById(R.id.begin);
    }

    @Override
    public void initData() {
        mHandler = new BulkHandler(this);
        mPresenter = new BulkRegistPresenterImpl(this);
        mTitle.setText(getResources().getString(R.string.bulkregist));
        mBeanList = new ArrayList<>();
        mFileList = new ArrayList<>();
        LinearLayoutManager detectRvManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(detectRvManager);
        mBulkAdapter = new BulkRegistAdapter(mBeanList,this);
        mRv.setAdapter(mBulkAdapter);
        mBulkAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        },mRv);
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mBegin.setOnClickListener(this);
        mBulkAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BulkRegistBean bulkRegistBean = mBeanList.get(position);
                if (bulkRegistBean.isRegist()) {
                    ToastUtils.showLong(getResources().getString(R.string.registered));
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("REGIST_FILE", bulkRegistBean.getFile().getPath());
                    intent.setClass(BulkRegistActivity.this, BmpRegistActvity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
        mBeanList.clear();
        mFileList.clear();
        mBulkAdapter.replaceData(mBeanList);
        mPresenter.refreshMsg();
        page = 1;
        showDialog(getResources().getString(R.string.load));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.begin:
                mPresenter.registAllUser();
                break;

            case R.id.back:
            case R.id.title:
                finishActivity();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    void finishActivity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BulkRegistActivity.this);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(getResources().getString(R.string.finish));
        builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                BulkRegistActivity.this.finish();
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
    public void refreshDone(List<File> list) {
        mFileList.addAll(list);
        mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
        mHandler.sendEmptyMessage(MSG_FILE_LIST);
        loadMore();
    }

    @Override
    public void refreshEmpty() {
        mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
        mHandler.sendEmptyMessage(MSG_FILE_EMPTY);
    }

    @Override
    public void registCheck(String name, String progress) {
        Message message = Message.obtain();
        message.what = MSG_UPDATE_DIALOG;
        message.obj = getResources().getString(R.string.check)+" : "+ name +" "+progress;
        mHandler.sendMessage(message);
    }

    @Override
    public void registBegin(String name, String progress) {
        Message message = Message.obtain();
        message.what = MSG_UPDATE_DIALOG;
        message.obj = getResources().getString(R.string.register)+" : "+ name +" "+progress;
        mHandler.sendMessage(message);
    }

    @Override
    public void registDone(String name) {
        Message message = Message.obtain();
        message.what = MSG_UPDATE_DIALOG;
        message.obj = getResources().getString(R.string.registered)+" : "+name;
        mHandler.sendMessage(message);
        mHandler.sendEmptyMessage(MSG_FILE_LIST_UPDATE);
    }

    @Override
    public void registFail(final String name, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(getResources().getString(R.string.register_fail)+" : "+name +" "+msg);
            }
        });
        mHandler.sendEmptyMessage(MSG_FILE_LIST_UPDATE);
        LogUtils.d(BulkRegistPresenterImpl.HEAD, "Register Fail : "+name +" "+msg);
    }

    @Override
    public void registAllDone() {
        mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG);
        mHandler.sendEmptyMessage(MSG_REFRESH);
    }

    int page = 1;
    void loadMore(){
        mPresenter.loadMore(page, new BulkRegistPresenterImpl.loadMoreListener() {
            @Override
            public void loadMoreComplete(final List<BulkRegistBean> bulkRegistBeans) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        mBulkAdapter.addData(bulkRegistBeans);
                        mBulkAdapter.loadMoreComplete();
                    }
                });
            }

            @Override
            public void loadMoreEnd() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBulkAdapter.loadMoreEnd();
                    }
                });
            }
        });
    }

    public static class BulkHandler extends Handler {

        final WeakReference<BulkRegistActivity> mActivity;

        public BulkHandler(BulkRegistActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case MSG_DISMISS_DIALOG:
                    mActivity.get().dissmisDialog();
                    break;

                case MSG_UPDATE_DIALOG:
                    String string = (String) msg.obj;
                    mActivity.get().showDialog(string);
                    break;

                case MSG_FILE_LIST_UPDATE:
                    mActivity.get().mBulkAdapter.replaceData(mActivity.get().mBeanList);
                    break;

                case MSG_FILE_EMPTY:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(mActivity.get().getResources().getString(R.string.file_path)+" : "+ AttConstants.PATH_BULK_REGISTRATION);
                    stringBuilder.append("\n");
                    stringBuilder.append("\n");
                    stringBuilder.append(mActivity.get().getResources().getString(R.string.dir_empty));
                    mActivity.get().mMsg.setText(stringBuilder.toString());
                    break;

                case MSG_FILE_LIST:
                    String text = mActivity.get().getResources().getString(R.string.file_path)+" : "+ AttConstants.PATH_BULK_REGISTRATION + "\n" +
                            mActivity.get().getResources().getString(R.string.file_size)+" : "+ mActivity.get().mFileList.size();
                    mActivity.get().mMsg.setText(text);
                    break;

                case MSG_REFRESH:
                    mActivity.get().refresh();
                    break;

            }
        }
    }
    
}
