package com.lxl.pad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.base.util.StringUtils;
import com.aiwinn.base.widget.CameraInterface;
import com.aiwinn.faceSDK.AgeInfo;
import com.aiwinn.faceSDK.FaceInfoBean;
import com.aiwinn.faceSDK.GenderInfo;
import com.aiwinn.faceSDK.SDKManager;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.DetectListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lxl.shop.AttApp;
import com.lxl.shop.InitActivity;
import com.lxl.shop.R;
import com.lxl.shop.common.AttConstants;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.service.SyncCustomerModelService;
import com.lxl.shop.ui.p.DetectPresenter;
import com.lxl.shop.ui.p.DetectPresenterImpl;
import com.lxl.shop.ui.v.DetectView;
import com.lxl.shop.utils.IOHelper;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StringUtil;
import com.lxl.shop.viewmodel.CustomerModel;
import com.lxl.shop.viewmodel.CustomerRecongizeResponse;
import com.lxl.shop.widget.MaskView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.lxl.shop.common.ShopConfig.AD_SHOW_TIME_INTERVAL;
import static com.lxl.shop.common.ShopConfig.RECOGNITION_DELAY_TIME;
import static com.lxl.shop.common.ShopConfig.RECOGNITION_DELAY_TIME_MAX;
import static com.lxl.shop.common.ShopConfig.SHOW_VIEW_PAGER_DELAY;
import static com.lxl.shop.common.ShopConfig.WELCOME_SHOW_TIME;
import static com.lxl.shop.common.ShopConstants.CATCH_CUSTOMER_PATH;

/**
 * com.aiwinn.faceattendance.ui.m
 * SnapShot
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class PadActivity extends InitActivity implements CameraInterface.CameraStateCallBack, DetectView {

    private RecyclerView mRecyclerView;
    private MaskView mMaskView;
    private SurfaceView mSurfaceView;
    private TextView mTextView;
    private ImageView mBack;

    private ViewPager mViewPager;
    private RelativeLayout mWelcomeView;
    private LinearLayout mWelcomeList;

    private DetectPresenter mPresenter;
    private List<ImageView> imageViewList = new ArrayList<>();
    private DetectHandler mHandler;
    private List<CustomerModel> welcomeCustomerList = new ArrayList<>();

    private int mPreviewWidth;
    private int mPreviewHeight;
    private int i = 0;

    private final static int MSG_DETECT_NO_FACE = 0;
    private final static int MSG_DETECT_FAIL = 1;
    private final static int MSG_DETECT_DATA = 2;
//    private final static int MSG_FACE = 3;

    boolean isFinish = false;
    MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();
    LogUtil log;

    private static class DetectHandler extends Handler {

        final WeakReference<PadActivity> mActivity;

        public DetectHandler(PadActivity detectActivity) {
            mActivity = new WeakReference<>(detectActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_DETECT_DATA:
                    mActivity.get().updateMessgae((String) msg.obj);
                    break;

                case MSG_DETECT_FAIL:
                    mActivity.get().updateMessgae(((Status) msg.obj).toString());
                    mActivity.get().mMaskView.clearRect();
                    break;

                case MSG_DETECT_NO_FACE:
                    mActivity.get().updateMessgae(mActivity.get().getResources().getString(R.string.no_face));
                    mActivity.get().mMaskView.clearRect();
                    break;

//                case MSG_FACE:
//                    UserModel userModel = (UserModel) msg.obj;
//                    //展示
//                    mActivity.get().showWelComeView(userModel);
//                    Glide.with(mActivity.get()).load(bitmap).into(mActivity.get().mImCompareSucc);
//                    break;

            }
        }
    }

    void updateMessgae(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("w = " + mPreviewWidth + " h = " + mPreviewHeight);
        stringBuilder.append("\n");
        stringBuilder.append(s);
        mTextView.setText(stringBuilder.toString());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_main_pad);
        initView();
        initAction();
        initListeners();
        log = LogUtil.getInstance(this);
        mHandler = new DetectHandler(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(PadActivity.this, SyncCustomerModelService.class);
                startService(intent);
            }
        }, 5000);

//        test();
    }

    private void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserBean userBean = new UserBean();
                userBean.userId = "1";
                recognitionOldCustomerHandler(userBean);


                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userBean = new UserBean();
                userBean.userId = "2";
                recognitionOldCustomerHandler(userBean);


                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userBean = new UserBean();
                userBean.userId = "3";
                recognitionOldCustomerHandler(userBean);

                try {
                    Thread.sleep(13000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userBean = new UserBean();
                userBean.userId = "4";
                recognitionOldCustomerHandler(userBean);

            }
        }).start();
    }

    @Override
    public void initData() {
        mPresenter = new DetectPresenterImpl(this);
        CameraInterface.getInstance().setCameraStateCallBack(this, mSurfaceView);
        LinearLayoutManager detectRvManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(detectRvManager);
        showDialog(getResources().getString(R.string.load_camera));
        openCamera(AttConstants.CAMERA_ID, AttConstants.CAMERA_DEGREE);
        initWelcome();
        IntentFilter intent = new IntentFilter();
        intent.addAction(ShopConstants.CATCH_CUSTOMER);
        registerReceiver(broadcastReceiver, intent);
    }

    private void initWelcome() {
        try {
            AssetManager assets = getAssets();
            String[] list = assets.list("ad");
            for (int i = 0; i < list.length; i++) {
                String s = list[i];
                InputStream open = assets.open("ad" + File.separator + s);
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                Drawable drawable = new BitmapDrawable(bitmap);
                ImageView imageView = new ImageView(this);
                imageView.setBackground(drawable);
                imageViewList.add(imageView);
            }
            Log.i("lxltest", "list:" + list.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hideWelComeView();
        MyAdapter adapter = new MyAdapter();
        mViewPager.setAdapter(adapter);
        showViewPager();
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            int select = (i++ % imageViewList.size());
                            mViewPager.setCurrentItem(select);
                        }
                    });
                    try {
                        Thread.sleep(AD_SHOW_TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PadActivity.this.finish();
            }
        });
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isTouchAction = true;
                    hideViewPager();
                    postShowViewPagerDelay();
                }
                return false;
            }
        });
    }

    private void postShowViewPagerDelay() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTouchAction) {
                    postShowViewPagerDelay();
                } else {
                    showViewPager();
                }

            }
        }, SHOW_VIEW_PAGER_DELAY);
    }

    boolean isTouchAction = false;//是否有点击和触发


    @Override
    protected void onResume() {
        super.onResume();
        if (mIsGranted) {
            initData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
        PadActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMaskView.unInit();
//        detectList.clear();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mPresenter = null;
        mSurfaceView = null;
        isFinish = true;
        ConfigLib.detectionFirstInitFlag = true;
        Intent intent = new Intent();
        intent.setClass(this, SyncCustomerModelService.class);
        stopService(intent);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void initView() {
        {
            AttApp.hideBottomUIMenu(this);
//        mOther = findViewById(R.id.other);
//            mChangeCamera = findViewById(R.id.exchangeCamera);
//        mTvRegister = findViewById(R.id.tvregister);
//        mTvCompare = findViewById(R.id.tvcompare);
//        mTvCompareCancel = findViewById(R.id.tvcomparecancel);
//        mTvCompareMsg = findViewById(R.id.tvcomparemsg);
//        mImCompareWait = findViewById(R.id.imcomparewait);
//        mImCompareSucc = findViewById(R.id.imcomparesucc);
            mSurfaceView = findViewById(R.id.sv);
            mRecyclerView = findViewById(R.id.rv);
            mBack = findViewById(R.id.back);
            mTextView = findViewById(R.id.message);
            mMaskView = findViewById(R.id.kcfmv);

            mWelcomeView = findViewById(R.id.welcome_view);
            mViewPager = findViewById(R.id.view_pager);
            mWelcomeList = findViewById(R.id.welcome_list);

            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
    }

    @Override
    protected void initAction() {

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        ToastUtils.showShort(getResources().getString(R.string.reopen_activity));
//        PadActivity.this.finish();
//    }

    @Override
    public void getCameraParameters() {
        mPreviewWidth = CameraInterface.getInstance().getPreviewWidth();
        mPreviewHeight = CameraInterface.getInstance().getPreviewHeight();
        FaceDetectManager.setDegree(AttConstants.CAMERA_DEGREE);
    }

    int initFrame = 1;
    int detectFrame = 4;
    int nowFrame = initFrame;
    boolean checkFrame = false;

    boolean check() {
        if (!checkFrame) {
            nowFrame++;
            dissmisDialog();
            if (nowFrame >= detectFrame) {
                nowFrame = initFrame;
                checkFrame = true;
            }
        }
        return checkFrame;
    }

    @Override
    public void cameraHasPreview(byte[] data, Camera camera) {
        LogUtils.d(DetectPresenterImpl.HEAD, "Begin -> ( w = " + mPreviewWidth + " h = " + mPreviewHeight + " size = " + data.length + " )");
        if (check()) {
            mPresenter.detectFaceData(data, mPreviewWidth, mPreviewHeight);
        }
    }

    @Override
    public void recognizeFace(final UserBean userBean) {
        Log.i("lxltest", "识别到用户！");
        recognitionOldCustomerHandler(userBean);
    }

    public void addCustomerModel(String customerId, String customerName, String gender, String userBeanLocalImg) {
        if (StringUtil.emptyOrNull(customerId)) {
            return;
        }
        CustomerModel currentCustomer = null;
        for (CustomerModel customer : welcomeCustomerList) {
            if (customerId.equals(customer.customerId)) {
                currentCustomer = customer;
                //移动顺序
                break;
            }
        }
        if (currentCustomer == null) {
            currentCustomer = new CustomerModel();
            currentCustomer.customerId = customerId;
            currentCustomer.gender = gender;
            currentCustomer.name = customerName;
            currentCustomer.userModel.localImagePath = userBeanLocalImg;
            if (welcomeCustomerList.size() >= 5) {
                welcomeCustomerList.remove(0);
            }
            welcomeCustomerList.add(currentCustomer);
        } else {
            welcomeCustomerList.remove(currentCustomer);
            welcomeCustomerList.add(currentCustomer);
        }
        refreshCustomerModel();
    }

    long tontinueTime = 0;

    public void refreshCustomerModel() {
        if (welcomeCustomerList.size() == 0) {
            showViewPager();
            return;
        }
        mWelcomeView.setVisibility(View.VISIBLE);
        mWelcomeList.removeAllViews();
        for (CustomerModel customerModel : welcomeCustomerList) {
            CharSequence welcomeDesc = getWelcomeDesc(customerModel.name, customerModel.gender);
            View inflate = View.inflate(this, R.layout.shop_welcome_item_view, null);
            TextView welcomeItemText = inflate.findViewById(R.id.welcome_item_text);
            ImageView imageView = inflate.findViewById(R.id.welcome_item_icon);
            Glide.with(this).load(customerModel.userModel.localImagePath).into(imageView);
            welcomeItemText.setText(welcomeDesc);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            mWelcomeList.addView(inflate, lp);
        }

        tontinueTime = System.currentTimeMillis() + WELCOME_SHOW_TIME;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - tontinueTime) >= 0) {
                    showViewPager();
                    welcomeCustomerList.clear();
                }
            }
        }, WELCOME_SHOW_TIME);
    }

    private CharSequence getWelcomeDesc(String customerName, String gender) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String genderStr = "女".equals(gender) ? "女士" : "先生";
        stringBuilder.append("欢迎 ");
        stringBuilder.append(customerName);
        stringBuilder.append(genderStr);

        int index = 3 + customerName.length();

        stringBuilder.setSpan(new TextAppearanceSpan(PadActivity.this, R.style.text_30_ffffff), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new TextAppearanceSpan(PadActivity.this, R.style.text_50_ffffff_b), 3, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new TextAppearanceSpan(PadActivity.this, R.style.text_30_ffffff), index, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return stringBuilder;
    }


    private void showViewPager() {
        hideWelComeView();
        isTouchAction = false;
        mViewPager.setVisibility(View.VISIBLE);
        mSurfaceView.getLayoutParams().height = 1;
    }

    private void hideViewPager() {
        mViewPager.setVisibility(View.GONE);
        mSurfaceView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    long recoginzeFaceNotMatchLastTime = 0;

    @Override
    public void recognizeFaceNotMatch(final UserBean userBean) {
        Log.i("lxltest", "recognizeFaceNotMatch");
        //匹配失败，则发送服务通知服务
        if (System.currentTimeMillis() - recoginzeFaceNotMatchLastTime < 3000) {
            return;
        }
        recoginzeFaceNotMatchLastTime = System.currentTimeMillis();

//        StockUtil.showToastOnMainThread(PadActivity.this, "匹配到新用户，通知服务");

        new Thread(new Runnable() {
            @Override
            public void run() {
                recognitionNewCustomerHandler(userBean);
            }
        }).start();
    }

    @Override
    public void detectNoFace() {
//        Log.i("lxltest", "detectNoFace");
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_DETECT_NO_FACE);
        }
    }

    @Override
    public void detectFail(Status status) {
        Log.i("lxltest", "detectFail:" + status.name());
        Message message = Message.obtain();
        message.what = MSG_DETECT_FAIL;
        message.obj = status;
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void detectFace(final List<FaceBean> faceBeans) {
        Log.i("lxltest", "detectFace:");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (faceBeans) {
                    List<FaceBean> faceBeanList = new ArrayList<>();
                    faceBeanList.clear();
                    faceBeanList.addAll(faceBeans);
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        for (int i = 0; i < faceBeanList.size(); i++) {
                            FaceBean bean = faceBeanList.get(i);
                            if (bean.mUserBean != null && !StringUtils.isEmpty(bean.mUserBean.name)) {
                                String name = bean.mUserBean.name;
                                stringBuilder.append("< Find " + name + " >");
                                stringBuilder.append("\n");
                            } else {
                                String find = "";
                                if (ConfigLib.detectWithLiveness) {
                                    String liveState = "";
                                    if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.UNKNOWN) {
                                        liveState = "UNKNOWN";
                                    } else if (bean.mLiveBean != null && bean.mLiveBean.livenessTag == bean.mLiveBean.FAKE) {
                                        liveState = "FAKE";
                                    }
                                    find = liveState;
                                } else {
                                    if (bean.mUserBean != null) {
                                        find = bean.mUserBean.compareScore + "";
                                    }
                                }
                                stringBuilder.append("< Find " + find + " >");
                                stringBuilder.append("\n");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendDebugMessage(stringBuilder.toString());
                    mMaskView.drawRect(faceBeanList, mPreviewWidth, mPreviewHeight);
                }
            }
        });
    }

    public void sendDebugMessage(String s) {
        Message message = Message.obtain();
        message.what = MSG_DETECT_DATA;
        message.obj = s;
        mHandler.sendMessage(message);
    }

    public void openCamera(final int id, final int degree) {
        switch (AttConstants.CAMERA_PREVIEW_HEIGHT) {

            case 0:
                CameraInterface.getInstance().setPreViewSize(0, 0);
                break;

            case 480:
                CameraInterface.getInstance().setPreViewSize(640, 480);
                break;

            case 720:
                CameraInterface.getInstance().setPreViewSize(1280, 720);
                break;

            case 1080:
                CameraInterface.getInstance().setPreViewSize(1920, 1080);
                break;

        }
        Thread openThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CameraInterface.getInstance().doOpenCamera(id, degree);
            }
        };
        openThread.start();
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            System.out.println("instantiateItem初始化: " + position);
            ImageView imageView = imageViewList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void hideWelComeView() {
        mWelcomeView.setVisibility(View.GONE);
    }

    //线程中调用
    private void recognitionNewCustomerHandler(UserBean userBean) {
        if (userBean.headImage == null) {
            log.logE("userName:" + userBean.name + ",headImage=null");
            return;
        }
        String localImagePath = IOHelper.saveBitmap(PadActivity.this, userBean.headImage);
        if (StringUtil.emptyOrNull(localImagePath)) {
            log.logE("userName:" + userBean.name + ",localImagePath=null");
            return;
        }
        File file = new File(localImagePath);
        String imgUrl = StockSender.getInstance().toUploadFile(file, new HashMap<String, String>());

        FaceInfoBean faceInfoBean = SDKManager.getInstance().getFaceDetectBeanByBitmap(userBean.headImage);
        AgeInfo[] ageInfos = faceInfoBean.getAgeInfos();
        GenderInfo[] genderInfos = faceInfoBean.getGenderInfos();

        if (StringUtil.emptyOrNull(imgUrl)) {
            return;
        }
        if (ageInfos == null) {
            return;
        }
        if (genderInfos == null) {
            return;
        }
        String faceId = JSON.toJSONString(userBean.features);
        int age = (int) ageInfos[0].age;
        String gender = genderInfos[0].gender > 0.5F ? "男" : "女";
        StockSender.getInstance().sendAddNewCustomerRecord(imgUrl, faceId, age, gender);
    }

    private void recognitionOldCustomerHandler(final UserBean userBean) {
        //发送请求给服务
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String customerId = getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0).getString(userBean.userId, "");
                final String customerName = getSharedPreferences(ShopConstants.SP_Register_CustomerName, 0).getString(userBean.userId, "");
                final String customerGender = getSharedPreferences(ShopConstants.SP_Register_CustomerGender, 0).getString(userBean.userId, "");
                final String userBeanLocalImg = userBean.localImagePath;

                //如果匹配到已经在展示列表当中，就返回，不在展示以及不发识别服务
                for (CustomerModel customerModel : welcomeCustomerList) {
                    if (customerId.equalsIgnoreCase(customerModel.customerId)) {
                        return;
                    }
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        addCustomerModel(customerId, customerName, customerGender, userBeanLocalImg);
                    }
                });
                //发送服务通知服务记录一下识别到了老客
                CustomerRecongizeResponse recongizeModel = StockSender.getInstance().sendAddRecongizeCustomer(customerId,"希特勒");
                if (StringUtil.emptyOrNull(customerId)) {
                    return;
                }
                final CustomerModel customerModel = StockSender.getInstance().sendSelectCustomerService(customerId);
                if (StringUtil.emptyOrNull(customerModel.customerId)) {
                    log.logE("customerId为空，faceId:" + userBean.userId);
                    return;
                }
            }
        }).start();
    }


    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getExtras() == null) {
                return;
            }
            final String path = intent.getExtras().getString(CATCH_CUSTOMER_PATH);
            log.LogW("onReceive:" + path);
            if (StringUtil.emptyOrNull(path)) {
                return;
            }
            final File file = new File(path);
            if (!(file.getName().endsWith("png") || file.getName().endsWith("jpg") || file.getName().endsWith("jpeg"))) {
                log.logE("onReceive:文件名有问题：" + path);
                return;
            }
            if (file.getName().endsWith("YT.jpg")) {
                log.logE("onReceive:YT图片不解析：" + path);
                return;
            }
            if (!file.exists()) {
                log.logE("onReceive:file不存在：");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //判断size是否有变化，没有变化继续下一步
                    long lastSize = 0;
                    long lastTime = System.currentTimeMillis();
                    while (lastSize != file.length()) {
                        if ((System.currentTimeMillis() - lastTime) > RECOGNITION_DELAY_TIME_MAX) {
                            break;
                        }
                        lastSize = file.length();
                        Log.i("lxltest", "lastSize:" + lastSize);
                        try {
                            Thread.sleep(RECOGNITION_DELAY_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    final Bitmap bitmap = ImageUtils.getBitmap(file);
                    if (bitmap == null) {
                        log.logE("onReceive:解析Bitmap错误：" + path);
                        return;
                    }
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    log.LogW("进行图片识别");
                    FaceDetectManager.detectFace(bitmap, new DetectListener() {
                        @Override
                        public void onSuccess(List<DetectBean> list) {
                            if (list.size() == 0) {
                                return;
                            }
                            final DetectBean detectBean = list.get(0);
                            RegisterBean registerBean = new RegisterBean();
                            registerBean.setName(UUID.randomUUID().toString());
                            log.LogW("进行图片注册");
                            //识别成功
                            FaceDetectManager.registerUser(bitmap, detectBean, registerBean, new RegisterListener() {
                                @Override
                                public void onSuccess(UserBean userBean) {
                                    //注册工程的话则删除
                                    log.LogW("识别是新用户，发送通知进行新用户注册，数据库中删除本次注册");
                                    recognitionNewCustomerHandler(userBean);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("x0",detectBean.x0);
                                    jsonObject.put("x1",detectBean.x1);
                                    jsonObject.put("y0",detectBean.y0);
                                    jsonObject.put("y1",detectBean.y1);
                                    jsonObject.put("width",bitmap.getWidth());
                                    jsonObject.put("height",bitmap.getHeight());
                                    jsonObject.put("center",(detectBean.x0+detectBean.x1)/2+","+(detectBean.y0+detectBean.y1)/2);
                                    FaceDetectManager.deleteByUserInfo(userBean);
                                }

                                @Override
                                public void onSimilarity(UserBean userBean) {
                                    String customerId = getSharedPreferences(ShopConstants.SP_Register_CustomerId, 0).getString(userBean.userId, "");
                                    if (StringUtil.emptyOrNull(customerId)) {
                                        return;
                                    }
                                    log.LogW("识别是老用户，发送老客进店通知服务");
                                    recognitionOldCustomerHandler(userBean);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("x0",detectBean.x0);
                                    jsonObject.put("x1",detectBean.x1);
                                    jsonObject.put("y0",detectBean.y0);
                                    jsonObject.put("y1",detectBean.y1);
                                    jsonObject.put("width",bitmap.getWidth());
                                    jsonObject.put("height",bitmap.getHeight());
                                    jsonObject.put("center",(detectBean.x0+detectBean.x1)/2+","+(detectBean.y0+detectBean.y1)/2);
                                    StockSender.getInstance().sendAddRecongizeCustomer(customerId,jsonObject.toString());
                                }

                                @Override
                                public void onError(Status status) {
                                    Log.i("lxltest", "status:" + status.name());

                                }
                            });

                        }

                        @Override
                        public void onError(Status status, String s) {

                        }
                    });


                }
            }).start();


        }
    }

}
