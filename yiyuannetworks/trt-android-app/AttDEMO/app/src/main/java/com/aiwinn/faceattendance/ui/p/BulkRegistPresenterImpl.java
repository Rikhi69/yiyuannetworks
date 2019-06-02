package com.aiwinn.faceattendance.ui.p;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.base.util.FileUtils;
import com.aiwinn.base.util.ImageUtils;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.bean.BulkRegistBean;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.faceattendance.ui.m.BulkRegistActivity;
import com.aiwinn.faceattendance.ui.v.BulkRegistView;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.Utils.ScaleBitmapUtils;
import com.aiwinn.facedetectsdk.Utils.ThreadPoolUtils;
import com.aiwinn.facedetectsdk.bean.DetectBean;
import com.aiwinn.facedetectsdk.bean.ImageResult;
import com.aiwinn.facedetectsdk.bean.RegisterBean;
import com.aiwinn.facedetectsdk.bean.UserBean;
import com.aiwinn.facedetectsdk.common.Status;
import com.aiwinn.facedetectsdk.listener.DetectListener;
import com.aiwinn.facedetectsdk.listener.RegisterListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * com.aiwinn.faceattendance.ui.p
 * SDK_ATT
 * 2018/09/03
 * Created by LeoLiu on User
 */

public class BulkRegistPresenterImpl implements BulkRegistPresenter {

    public static final String HEAD = "ATT_BULK";

    private final BulkRegistView mView;
    private List<DetectBean> mDetectBeans;
    private List<File> mFiles;

    public BulkRegistPresenterImpl(BulkRegistView view) {
        mView = view;
        mFiles = new ArrayList<>();
        mDetectBeans = new ArrayList<>();
    }

    public interface loadMoreListener{

        void loadMoreComplete(List<BulkRegistBean> mBeanList);

        void loadMoreEnd();

    }

    int pageCount = 10;
    int endDataIndex = 0;

    public void loadMore(final int page, final loadMoreListener listener) {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                if (((page - 1) * pageCount) > mFiles.size()) {
                    listener.loadMoreEnd();
                }else {
                    int end = page * pageCount;
                    if (end > mFiles.size()) {
                        end = mFiles.size();
                    }
                    List<BulkRegistBean> call = new ArrayList<>();
                    call.clear();
                    List<File> files = new ArrayList<>();
                    files.clear();
                    files.addAll(mFiles.subList(endDataIndex,end));
                    endDataIndex = end;
                    for (int i = 0; i < files.size(); i++) {
                        File file = files.get(i);
                        boolean isRegist;
                        List<UserBean> userBeanList = FaceDetectManager.queryByName(getName(file.getName()));
                        if (userBeanList.size() > 0) {
                            isRegist = true;
                        }else {
                            isRegist = false;
                        }
                        BulkRegistBean registBean = new BulkRegistBean(file,isRegist);
                        call.add(registBean);
                    }
                    listener.loadMoreComplete(call);
                }
            }
        });
    }

    @Override
    public void refreshMsg() {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                mFiles.clear();
                mDetectBeans.clear();
                endDataIndex = 0;
                List<File> list = FileUtils.listFilesInDir(AttConstants.PATH_BULK_REGISTRATION);
                for (File file : list) {
                    if (!ImageUtils.isImage(file)) {
                        LogUtils.d(HEAD,"Found is not image : "+file.getName());
                        mFiles.add(file);
                    }
                }
                list.removeAll(mFiles);
                mFiles.clear();
                mFiles.addAll(list);
                if (list.size() <= 0) {
                    mView.refreshEmpty();
                }else {
                    mView.refreshDone(list);
                }
            }
        });
    }

    @Override
    public void registAllUser() {
        ThreadPoolUtils.executeTask(new Runnable() {
            @Override
            public void run() {
                int mAll = mFiles.size();
                for (int k = 0; k < mFiles.size(); k++) {
                    File file = mFiles.get(k);
                    final String fileName = getName(file.getName());
                    LogUtils.d(HEAD,"Begin check : "+ fileName +" { "+(k+1) +" / "+ mAll +" }");
                    mView.registCheck(fileName," { "+(k+1) +" / "+ mAll +" }");
                    boolean isRegist = false;
                    List<UserBean> userBeanList = FaceDetectManager.queryByName(fileName);
                    if (userBeanList.size() > 0) {
                        isRegist = true;
                    }
                    if (!isRegist) {
                        LogUtils.d(HEAD,"Begin register : "+ fileName +" { "+(k+1) +" / "+ mAll +" }");
                        mView.registBegin(fileName," { "+(k+1) +" / "+ mAll +" }");
                        ImageResult imageResult = ScaleBitmapUtils.scaleBitmap(file);
                        if (imageResult != null && imageResult.scalePic != null) {
                            FaceDetectManager.detectFace(imageResult.scalePic, new DetectListener(){

                                @Override
                                public void onSuccess(List<DetectBean> detectBeanList) {
                                    mDetectBeans.clear();
                                    mDetectBeans.addAll(detectBeanList);
                                }

                                @Override
                                public void onError(Status code, String msg) {
                                    mView.registFail(fileName,code.toString());
                                }
                            });
                            if (mDetectBeans == null || mDetectBeans.size() <= 0) {
                                mView.registFail(fileName,"FaceResult is Null");
                            }else {

                                //人脸位置， 还原映射为大图的
                                for (int i = 0; i < mDetectBeans.size(); i++) {
                                    mDetectBeans.get(i).x0 =  mDetectBeans.get(i).x0*imageResult.scalePicWidth;
                                    mDetectBeans.get(i).x1 =  mDetectBeans.get(i).x1*imageResult.scalePicWidth;
                                    mDetectBeans.get(i).y0 =  mDetectBeans.get(i).y0*imageResult.scalePicHeight;
                                    mDetectBeans.get(i).y1 =  mDetectBeans.get(i).y1*imageResult.scalePicHeight;

                                    ArrayList<Float> landmarkscl = mDetectBeans.get(i).landmarks;
                                    ArrayList<Float> landmarks =  new ArrayList();
                                    landmarks = (ArrayList<Float> )landmarkscl.clone();

                                    mDetectBeans.get(i).landmarks.clear();  // 提取特征值 需要依靠 五个点的值
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(0) * imageResult.scalePicWidth);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(1) * imageResult.scalePicHeight);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(2) * imageResult.scalePicWidth);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(3) * imageResult.scalePicHeight);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(4) * imageResult.scalePicWidth);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(5) * imageResult.scalePicHeight);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(6) * imageResult.scalePicWidth);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(7) * imageResult.scalePicHeight);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(8) * imageResult.scalePicWidth);
                                    mDetectBeans.get(i).landmarks.add(landmarks.get(9) * imageResult.scalePicHeight);
                                }

                                if (mDetectBeans.size() > 0) {
                                    int MaxNum = 0;
                                    for (int j = 0; j < mDetectBeans.size(); j++) {  //获取最大的人脸框
                                        if ((mDetectBeans.get(j).y1 - mDetectBeans.get(j).y0) > (mDetectBeans.get(MaxNum).y1 - mDetectBeans.get(MaxNum).y0)) {
                                            MaxNum = j;
                                        }
                                    }
                                    //开始注册图片中最大的人脸
                                    RegisterBean registerBean = new RegisterBean(fileName);
                                    FaceDetectManager.registerUser(imageResult.image, mDetectBeans.get(MaxNum), registerBean, new RegisterListener() {
                                        @Override
                                        public void onSuccess(UserBean userBean) {
                                            mView.registDone(fileName);
                                        }

                                        @Override
                                        public void onSimilarity(UserBean userBean) {
                                            LogUtils.d(HEAD,"register face Error . Similarity : "+userBean.name+" "+userBean.userId);
                                        }

                                        @Override
                                        public void onError(Status status) {
                                            LogUtils.d(HEAD,"register face Error . status : "+status.toString());
                                            mView.registFail(fileName,status.toString());
                                        }
                                    });
                                } else {
                                    mView.registFail(fileName,((BulkRegistActivity)mView).getString(R.string.no_face));
                                }
                            }
                        }else {
                            mView.registFail(fileName,"ImageResult is Null");
                        }
                    }else {
                        LogUtils.d(HEAD,"had register : "+ fileName +" { "+(k+1) +" / "+ mAll +" }");
                    }
                    LogUtils.d(HEAD,"End register : "+fileName);
                }
                mView.registAllDone();
            }
        });
    }

    public static String getName(String oldName){
        String[] strings = oldName.split("\\.");
        return strings[0];
    }

}
