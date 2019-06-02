package com.aiwinn.faceattendance.bean;

import com.aiwinn.facedetectsdk.bean.UserBean;

/**
 * com.aiwinn.faceattendance.bean
 * SDK_ATT
 * 2018/10/18
 * Created by LeoLiu on User
 */

public class RegisterListBean {

    UserBean mUserBean;
    boolean choose;
    boolean show;

    public RegisterListBean(UserBean userBean, boolean choose, boolean show) {
        mUserBean = userBean;
        this.choose = choose;
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public UserBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
