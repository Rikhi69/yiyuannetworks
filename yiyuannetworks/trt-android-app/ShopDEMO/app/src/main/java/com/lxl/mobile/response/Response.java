package com.lxl.mobile.response;


import java.util.List;

/**
 * Created by zly on 2017/2/9.
 */

public class Response {
    private String action;
    private Object object;
    private List dataList;

    public String getAction() {
        return action;
    }

    public Object getObject() {
        return object;
    }

    public List getDataList() {
        return dataList;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;
    }
}
