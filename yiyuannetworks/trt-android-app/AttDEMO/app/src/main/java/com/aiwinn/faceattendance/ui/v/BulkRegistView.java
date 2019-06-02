package com.aiwinn.faceattendance.ui.v;

import java.io.File;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.v
 * SDK_ATT
 * 2018/09/03
 * Created by LeoLiu on User
 */

public interface BulkRegistView {

    void refreshDone(List<File> list);

    void refreshEmpty();

    void registCheck(String name, String progress);

    void registBegin(String name, String progress);

    void registDone(String name);

    void registFail(String name, String msg);

    void registAllDone();

}
