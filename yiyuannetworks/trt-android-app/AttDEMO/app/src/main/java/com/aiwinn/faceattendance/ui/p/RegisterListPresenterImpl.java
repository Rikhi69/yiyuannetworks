package com.aiwinn.faceattendance.ui.p;

import com.aiwinn.faceattendance.ui.v.RegisterListView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/28
 * Created by LeoLiu on User
 */

public class RegisterListPresenterImpl implements RegisterListPresenter {


    RegisterListView mView;
    private List<UserBean> mUserBeanList = new ArrayList<>();

    public RegisterListPresenterImpl(RegisterListView registerListView) {
        mView = registerListView;
    }

    public interface loadMoreListener{

        void loadMoreComplete(List<UserBean> mBeanList);

        void loadMoreEnd();

        void loadAll(List<UserBean> mBeanList);

    }

    int pageCount = 30;
    int endDataIndex = 0;

    @Override
    public void loadMore(final int page, final loadMoreListener listener) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    endDataIndex = 0;
                    mUserBeanList.clear();
                    mUserBeanList.addAll(FaceDetectManager.queryAll());
                    listener.loadAll(mUserBeanList);
                }
                if (((page - 1) * pageCount) < mUserBeanList.size()) {
                    int end = page * pageCount;
                    if (end > mUserBeanList.size()) {
                        end = mUserBeanList.size();
                    }
                    List<UserBean> call = new ArrayList<>();
                    call.clear();
                    call.addAll(mUserBeanList.subList(endDataIndex,end));
                    endDataIndex = end;
                    listener.loadMoreComplete(call);
                }else {
                    listener.loadMoreEnd();
                }
            }
        });
    }

    @Override
    public void deleteFace(final int index,final UserBean user) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                if (FaceDetectManager.deleteByUserInfo(user)) {
                    mView.deleteDone(index,user);
                }else {
                    mView.deleteFail(user);
                }
            }
        });
    }

    @Override
    public void deleteAll() {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                boolean result = FaceDetectManager.deleteAll();
                mView.deleteAllDone(result);
            }
        });
    }

    @Override
    public void delete(final List<UserBean> userBeans) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                boolean result = FaceDetectManager.deleteByUserInfo(userBeans);
                mView.deleteAllDone(result);
            }
        });
    }
}
