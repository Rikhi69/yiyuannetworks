package com.yiyuan.ai.model;

/**
 * Created by wangyu on 2019/4/10.
 */

public class MainModel {

    private String name;

    private int resId;

    private String color;

    public MainModel(){

    }

    public MainModel(String name,int resId,String color){
        this.name = name;
        this.resId = resId;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
