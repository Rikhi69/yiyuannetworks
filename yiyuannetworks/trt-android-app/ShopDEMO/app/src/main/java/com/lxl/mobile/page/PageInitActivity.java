package com.lxl.mobile.page;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lxl.shop.R;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.ui.HotelCustomDialog;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.utils.StockUtil;
import com.lxl.shop.viewmodel.CustomerRecentModel;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by yanglei on 2018/11/24.
 */

public abstract class PageInitActivity extends FragmentActivity {
    protected Handler mHander = new Handler();
    MyBroadcastReceiver receiver = new MyBroadcastReceiver();
    private boolean isFront = false;
    private AlertDialog dialog;
    private HotelCustomDialog customDialog;
    protected LogUtil log;

    protected void registerCustomerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShopConstants.USER_BROADCAST);
        registerReceiver(receiver, filter);
    }

    protected void unRegisterCustomerBroadcast() {
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = LogUtil.getInstance(this);
        registerCustomerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterCustomerBroadcast();
    }

    protected abstract void initView();

    protected abstract void initAction();

    protected abstract void initData();

    protected abstract void initListeners();

    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (!isFront) {
                StockUtil.showToastOnMainThread(context, "有老会员进店！");
                log.LogW("有老客进店，不再前台，给予Toast提示");
                return;
            }
            List<CustomerRecentModel> customerModelList = (List<CustomerRecentModel>) intent.getExtras().getSerializable(ShopConstants.CUSTOMER_RECENT_LIST);
            if (customerModelList.size() == 0) {
                log.LogW("有老客进店，长度为0");
                return;
            }
            final String customerId = customerModelList.get(0).customerVO.customerId;
            showNotification(customerId);

//            if (customDialog != null && customDialog.isAdded()) {
//                return;
//            }
//            customDialog = new HotelCustomDialog();
//            customDialog.setContent("有老客户进店，是否进入客服详情？", "进入", "取消");
//            customDialog.setDialogBtnClick(new HotelCustomDialog.HotelDialogBtnClickListener() {
//                @Override
//                public void leftBtnClick(HotelCustomDialog dialog) {
//                    Intent start = new Intent();
//                    start.putExtra(ShopConstants.CUSTOMERID, customerId);
//                    start.setClass(context, CustomerMainPage.class);
//                    context.startActivity(start);
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void rightBtnClick(HotelCustomDialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//            customDialog.show(getSupportFragmentManager(), "show");
        }
    }

    protected void showNotification(String customerId) {

        String channelId = "channel_id_shop";
        String channelName = "channel_name_shop";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        Intent start = new Intent();
        start.putExtra(ShopConstants.CUSTOMERID, customerId);
        start.setClass(this, CustomerMainPage.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, start, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        long[] str = new long[]{
                1000, 2000, 3000
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(str);
        } else {

        }
        showRing();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("有会员进店")
                .setDefaults(defaults)
                .setSmallIcon(R.drawable.admin_icon)
                .setContentIntent(mainPendingIntent)
                .setOngoing(true).setChannelId(channelId).setAutoCancel(true).setVibrate(str);
        Notification notification = notificationBuilder.build();
//        notification.sound = sound;
        notificationManager.notify(111123, notification);
    }

    private void showRing() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        AssetFileDescriptor file = getApplication().getResources().openRawResourceFd(R.raw.burst);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFront = false;
    }

    public void showDialog(String var1) {
        if (this.dialog == null) {
            this.dialog = new ProgressDialog(this);
        }

        this.dialog.setMessage(var1);
        if (!this.dialog.isShowing()) {
            this.dialog.show();
        }

    }

    public void dissmisDialog() {
        if (this.dialog != null && dialog.isShowing()) {
            this.dialog.dismiss();
        }

    }
}
