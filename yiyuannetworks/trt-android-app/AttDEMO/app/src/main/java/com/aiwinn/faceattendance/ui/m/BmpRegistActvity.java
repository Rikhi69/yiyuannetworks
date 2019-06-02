package com.aiwinn.faceattendance.ui.m;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.gallery.DefaultCallback;
import com.aiwinn.base.gallery.EasyImage;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.ui.p.BmpRegistPresenter;
import com.aiwinn.faceattendance.ui.p.BmpRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.p.BulkRegistPresenterImpl;
import com.aiwinn.faceattendance.ui.v.BmpRegistView;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.m
 * SnapShot
 * 2018/08/29
 * Created by LeoLiu on User
 */

public class BmpRegistActvity extends BaseActivity implements View.OnClickListener ,BmpRegistView {

    ImageView mBack;
    TextView mTitle;
    TextView mChoose;
    TextView mStart;
    ImageView mImage;
    EditText mName;

    final static int MSG_REGIST_NO_FACE = 0;
    final static int MSG_REGIST_FAIL = 1;
    final static int MSG_REGIST_SUCC = 2;

    boolean hasImage;
    private File mChooseImage;
    private BmpRegistPresenter mPresenter;
    private RegistHandler mHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bmpregist;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initViews() {
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mChoose = findViewById(R.id.choose);
        mStart = findViewById(R.id.start);
        mImage = findViewById(R.id.image);
        mName = findViewById(R.id.name);
    }

    @Override
    public void initData() {
        hasImage = false;
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("REGIST_FILE");
        if (!StringUtils.isEmpty(filePath)) {
            hasImage = true;
            mChooseImage = new File(filePath);
            mName.setText(BulkRegistPresenterImpl.getName(mChooseImage.getName()));
            loadImage();
        }
        mHandler = new RegistHandler(this);
        mPresenter = new BmpRegistPresenterImpl(this);
        mTitle.setText(getResources().getString(R.string.bmpregist));
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mStart.setOnClickListener(this);
        mChoose.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(BmpRegistActvity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        if (returnedPhotos != null && returnedPhotos.size() >0) {
            mChooseImage = returnedPhotos.get(0);
            hasImage = true;
            loadImage();
        }
    }

    private void loadImage() {
        Glide.with(this).load(mChooseImage).into(mImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.choose:
                EasyImage.openGallery(BmpRegistActvity.this, 0);
                break;

            case R.id.start:
                if (!hasImage || mChooseImage == null) {
                    ToastUtils.showLong(getResources().getString(R.string.choose));
                    return;
                }
                String name = mName.getText().toString().trim();
                if (StringUtils.isEmpty(name)) {
                    ToastUtils.showLong(getResources().getString(R.string.name_empty));
                    return;
                }
                showDialog(getResources().getString(R.string.register)+" : "+name);
                mPresenter.registUser(mChooseImage,name);
                break;

            case R.id.back:
            case R.id.title:
                BmpRegistActvity.this.finish();
                break;

        }
    }

    @Override
    public void registSucc(UserBean userBean) {
        Message message = Message.obtain();
        message.what = MSG_REGIST_SUCC;
        message.obj = userBean;
        mHandler.sendMessage(message);
    }

    @Override
    public void registFail(Status status) {
        Message message = Message.obtain();
        message.what = MSG_REGIST_FAIL;
        message.obj = status;
        mHandler.sendMessage(message);
    }

    @Override
    public void noFace() {
        mHandler.sendEmptyMessage(MSG_REGIST_NO_FACE);
    }

    static class RegistHandler extends Handler {

        final WeakReference<BmpRegistActvity> mActivity;

        public RegistHandler(BmpRegistActvity detectActivity) {
            mActivity = new WeakReference<>(detectActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case MSG_REGIST_NO_FACE:
                    mActivity.get().dissmisDialog();
                    ToastUtils.showShort(mActivity.get().getResources().getString(R.string.no_face));
                    break;

                case MSG_REGIST_FAIL:
                    mActivity.get().dissmisDialog();
                    Status status = (Status) msg.obj;
                    ToastUtils.showLong(status.toString());
                    break;

                case MSG_REGIST_SUCC:
                    mActivity.get().dissmisDialog();
                    UserBean result = (UserBean) msg.obj;
                    mActivity.get().registerResult(result);
                    break;

            }
            mActivity.get().dissmisDialog();
        }
    }

    private void registerResult(UserBean result) {
        ToastUtils.showShort(getResources().getString(R.string.register_success)+" : "+result.name);
        BmpRegistActvity.this.finish();
    }

}
