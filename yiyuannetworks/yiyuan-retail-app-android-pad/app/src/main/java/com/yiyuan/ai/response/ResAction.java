package com.yiyuan.ai.response;

public enum  ResAction {
    INIT_DATA("init", 1, "初始化全部数据"),
    PULL_DATA("pull", 2, "根据通知拉取数据"),
    SAVE_DATA("save", 3, "根据通知新增数据"),
    DELETE_DATA("delete", 4, "根据通知删除数据"),
    UPDATE_DATA("update", 5, "根据通知更新数据");

    private String action;
    private int reqEvent;
    private String desc;


    ResAction(String action, int reqEvent, String respClazz) {
        this.action = action;
        this.reqEvent = reqEvent;
        this.desc = respClazz;
    }


    public String getAction() {
        return action;
    }


    public int getReqEvent() {
        return reqEvent;
    }

}
