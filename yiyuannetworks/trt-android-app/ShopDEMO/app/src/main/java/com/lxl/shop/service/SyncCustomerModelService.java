package com.lxl.shop.service;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import com.aiwinn.facedetectsdk.FaceDetectManager;

import java.io.File;

import static com.lxl.shop.common.ShopConstants.CATCH_CUSTOMER;
import static com.lxl.shop.common.ShopConstants.CATCH_CUSTOMER_PATH;
import static com.lxl.shop.common.ShopConstants.SD_RECOGNITION_FOLDER;

/**
 * Created by yanglei on 2018/11/24.
 */

public class SyncCustomerModelService extends Service {
    boolean isFinish = false;
    SyncCustomerServiceImpl syncService;
    SDCardListener listener;

    public SyncCustomerModelService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //拉取所有注册用户信息
        FaceDetectManager.init(this);
        syncService = new SyncCustomerServiceImpl(this);
        listener = new SDCardListener(SD_RECOGNITION_FOLDER);
        //开始监听
        listener.startWatching();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("lxltest", "SyncCustomerModelService onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    int sleep = syncService.syncCustomerService();
                    Log.i("lxltest", "sleep:" + sleep);
                    try {
                        Thread.sleep(sleep);
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
        System.out.println("停止服务");
        listener.stopWatching();
        super.onDestroy();
    }


    public class SDCardListener extends FileObserver {

        public SDCardListener(String path) {
                 /*
                  * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
                  * 则int参数是要监听的事件类型.
                  */
            super(path);
            Log.w("lxltest", "path:" + path);
        }

        @Override
        public void onEvent(int event, String path) {
            Log.w("lxltest", "onEvent path:" + path);
            switch (event) {
                case FileObserver.ALL_EVENTS:
                    Log.w("lxltest", "ALL_EVENTS path:" + path);
                    break;
                case FileObserver.CREATE:
                    Log.w("lxltest", "CREATE path:" + path);
                    //创建文件，发送广播通知PadActivity进行图片人脸识别，如果识别到则通知服务有老客进店。否则进入新客流程
                    Intent intent = new Intent(CATCH_CUSTOMER);
                    intent.putExtra(CATCH_CUSTOMER_PATH, SD_RECOGNITION_FOLDER + File.separator + path);
                    sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
