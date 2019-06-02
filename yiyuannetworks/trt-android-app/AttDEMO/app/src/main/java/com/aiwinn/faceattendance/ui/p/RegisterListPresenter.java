package com.aiwinn.faceattendance.ui.p;

import com.aiwinn.facedetectsdk.bean.UserBean;

import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public interface RegisterListPresenter {

    void loadMore(int page, RegisterListPresenterImpl.loadMoreListener loadMoreListener);

    void deleteFace(int index,UserBean face);

    void deleteAll();

    void delete(List<UserBean> userBeans);
}
