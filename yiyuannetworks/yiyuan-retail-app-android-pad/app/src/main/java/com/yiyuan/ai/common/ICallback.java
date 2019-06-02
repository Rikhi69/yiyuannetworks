package com.yiyuan.ai.common;

/**
 * Created by zly on 2017/6/12.
 */

public interface ICallback<T> {

    void onSuccess(T t);

    void onFail(String msg);

}
