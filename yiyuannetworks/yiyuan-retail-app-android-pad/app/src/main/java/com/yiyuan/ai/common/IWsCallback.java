package com.yiyuan.ai.common;


import com.yiyuan.ai.request.Action;
import com.yiyuan.ai.request.Request;

/**
 * Created by zly on 2017/7/23.
 */

public interface IWsCallback<T> {
    void onSuccess(T t);
    void onError(String msg, Request request, Action action);
    void onTimeout(Request request, Action action);
}
