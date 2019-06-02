package com.aiwinn.gaa;


import android.app.Application;

import com.aiwinn.faceSDK.ResultMessage;
import com.aiwinn.faceSDK.SDKManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ResultMessage resultMessage = SDKManager.getInstance().init(getApplicationContext());
        if (resultMessage != ResultMessage.CodeMessage_0){
            AppConfig.INIT_STATE = false;
            //init fail
        }else{
            //init success
            AppConfig.INIT_STATE = true;
        }
    }
}
