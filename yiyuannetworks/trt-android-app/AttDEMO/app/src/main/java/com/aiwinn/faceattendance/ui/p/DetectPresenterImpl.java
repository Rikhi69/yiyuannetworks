package com.aiwinn.faceattendance.ui.p;

import com.aiwinn.base.util.StringUtils;
import com.aiwinn.faceattendance.ui.v.DetectView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.bean.FaceBean;
import com.aiwinn.facedetectsdk.bean.LivenessBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.RecognizeListener;

import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class DetectPresenterImpl implements DetectPresenter {

    public static final String HEAD = "ATT_DETECT";

    private DetectView mDetectView;

    public DetectPresenterImpl(DetectView detectView) {
        mDetectView = detectView;
    }

    @Override
    public void detectFaceData(final byte[] data, final int w, final int h) {
        FaceDetectManager.recognizeFace(data, w, h, new RecognizeListener() {
            @Override
            public void onDetectFace(List<FaceBean> faceBeanList) {
                if (faceBeanList.size() > 0) {
                    mDetectView.detectFace(faceBeanList);
                } else {
                    mDetectView.detectNoFace();
                }
            }

            @Override
            public void onLiveness(LivenessBean livenessBean) {

            }

            @Override
            public void onRecognize(UserBean userBean) {
                if (StringUtils.isEmpty(userBean.name)) {
                    mDetectView.recognizeFaceNotMatch(userBean);
                }else {
                    mDetectView.recognizeFace(userBean);
                }
            }

            @Override
            public void onError(Status status) {
                mDetectView.detectFail(status);
            }
        });
    }
}
