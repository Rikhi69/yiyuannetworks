package com.lxl.mobile.common;


import com.lxl.mobile.request.Action;
import com.lxl.mobile.request.Request;

/**
 * Created by zly on 2017/7/23.
 */

public interface IWsCallback<T> {
    void onSuccess(T t);
    void onError(String msg, Request request, Action action);
    void onTimeout(Request request, Action action);
}
