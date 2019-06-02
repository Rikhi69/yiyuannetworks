package com.yiyuan.ai.retrofit.model;


import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.yiyuan.ai.retrofit.api.VersionApi;
import com.yiyuan.ai.retrofit.response.VersionResponse;

import rx.Observable;
import rx.Subscriber;

import static com.yiyuan.ai.AIConstants.mBaseUrl;


/**
 * Created by wangyu on 2019/4/24.
 * Retrofit + RxJAVA接口访问
 */

public class VersionModel {

    private VersionModel() {
    }
    private static class SingletonInstance {
        private static final VersionModel INSTANCE = new VersionModel();
    }

    public static VersionModel getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 检查版本号
     * @param subscriber
     */
    public void queryVersion(Subscriber<VersionResponse> subscriber) {
        Observable<VersionResponse> observable = RetrofitSingle.getInstance().getRetrofitApi(VersionApi.class).getVersion();
        RetrofitSingle.getInstance().toSubscribe(observable, subscriber);
    }
}
