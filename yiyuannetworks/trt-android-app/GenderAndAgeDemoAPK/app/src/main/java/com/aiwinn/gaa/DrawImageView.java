package com.aiwinn.gaa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.aiwinn.faceSDK.AgeInfo;
import com.aiwinn.faceSDK.FaceInfo;
import com.aiwinn.faceSDK.FaceInfoBean;
import com.aiwinn.faceSDK.GenderInfo;

/**
 * Created by karl.wang on 2018/3/9.
 */
public class DrawImageView extends android.support.v7.widget.AppCompatImageView {

    private FaceInfo[] mFaceInfos;
    private AgeInfo[] mAgeInfos;
    private GenderInfo[] mGenderInfos;
    private Paint mPaint;
    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);
        mPaint.setAlpha(180);
    }

    public void setFaceDetectBean(FaceInfoBean faceDetectBean) {

        mFaceInfos = faceDetectBean.getFaceInfos();
        mAgeInfos = faceDetectBean.getAgeInfos();
        mGenderInfos = faceDetectBean.getGenderInfos();
    }

    public void setFaceInfo(FaceInfo[] FaceInfos) {

        mFaceInfos = FaceInfos;
    }

    public void setAgeInfos(AgeInfo[] AgeInfos) {

        mAgeInfos = AgeInfos;
    }

    public void setGenderInfos(GenderInfo[] GenderInfos) {

        mGenderInfos = GenderInfos;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mFaceInfos != null){
            int src_w = AppConfig.CAMERA_W;
            int src_h = AppConfig.CAMERA_H;
            for (int i = 0; i < mFaceInfos.length; i++) {
                Rect face1 = new Rect();

                if (AppConfig.cameraID == 1){
                    face1.right  = this.getWidth() - mFaceInfos[i].rect.left   *this.getWidth()/src_w;
                    face1.left  = this.getWidth() - mFaceInfos[i].rect.right *this.getWidth()/src_w;
                }else{
                    face1.left  =mFaceInfos[i].rect.left   *this.getWidth()/src_w;
                    face1.right  = mFaceInfos[i].rect.right *this.getWidth()/src_w;
                }

                face1.top = mFaceInfos[i].rect.top *this.getHeight()/src_h;
                face1.bottom  = mFaceInfos[i].rect.bottom*this.getHeight()/src_h;

                canvas.drawRect(face1, mPaint);


                String gender = mGenderInfos[i].gender == 0 ?  "女" : "男" ;
                if (face1.top-30f < 0) {
                    canvas.drawText("年龄:"+((int)mAgeInfos[i].age)+" 性别:"+gender,face1.left,face1.bottom+30f,mPaint);
                }else {
                    canvas.drawText("年龄:"+((int)mAgeInfos[i].age)+" 性别:"+gender,face1.left, face1.top-10f,mPaint);
                }
            }
        }
        super.onDraw(canvas);
    }


}