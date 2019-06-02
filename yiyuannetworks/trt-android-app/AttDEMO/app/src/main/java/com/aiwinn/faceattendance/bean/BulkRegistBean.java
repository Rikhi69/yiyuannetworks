package com.aiwinn.faceattendance.bean;

import java.io.File;

/**
 * com.aiwinn.faceattendance.bean
 * SDK_ATT
 * 2018/09/03
 * Created by LeoLiu on User
 */

public class BulkRegistBean {

    File mFile;
    boolean isRegist;

    public BulkRegistBean(File file, boolean isRegist) {
        mFile = file;
        this.isRegist = isRegist;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public boolean isRegist() {
        return isRegist;
    }

    public void setRegist(boolean regist) {
        isRegist = regist;
    }
}
