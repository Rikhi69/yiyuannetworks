package com.lxl.shop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.lxl.shop.common.ShopConfig;
import com.lxl.shop.sender.StockSender;
import com.lxl.shop.utils.LogUtil;
import com.lxl.shop.viewmodel.CustomerRecentModel;

import java.util.ArrayList;
import java.util.List;

import static com.lxl.shop.common.ShopConstants.CUSTOMER_RECENT_LIST;
import static com.lxl.shop.common.ShopConstants.USER_BROADCAST;

/**
 * Created by yanglei on 2018/11/24.
 */

public class ListenerCustomerInService extends Service {
    boolean isFinish = false;
    LogUtil log;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log = LogUtil.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.LogW("客人进店后台监听进程启动");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    long l = FaceDetectManager.queryCount();
                    if (l == 0) {
                        log.LogW("FaceDetectManager 还未初始化");
                    } else {
                        List<CustomerRecentModel> customerModels = StockSender.getInstance().sendSelectRecentCustomerService();
                        ArrayList list = new ArrayList();
                        list.addAll(customerModels);
                        if (customerModels.size() > 0) {
                            //发出通知，弹出对话框
                            Intent intent = new Intent(USER_BROADCAST);
                            intent.putExtra(CUSTOMER_RECENT_LIST, list);
                            sendBroadcast(intent);
                        } else {
                            log.LogW("返回进店客人长度为0，不提示");
                        }
                    }
                    try {
                        Thread.sleep(ShopConfig.SYNC_CUSTOMER_VISIT_TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isFinish = true;
        log.LogW("客人进店后台监听进程关闭");
        super.onDestroy();
    }

}
