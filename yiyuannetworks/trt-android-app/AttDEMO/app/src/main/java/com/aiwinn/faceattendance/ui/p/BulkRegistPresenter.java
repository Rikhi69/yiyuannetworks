package com.aiwinn.faceattendance.ui.p;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/09/03
 * Created by LeoLiu on User
 */

public interface BulkRegistPresenter {

    void loadMore(int page, BulkRegistPresenterImpl.loadMoreListener loadMoreListener);

    void refreshMsg();

    void registAllUser();

}
