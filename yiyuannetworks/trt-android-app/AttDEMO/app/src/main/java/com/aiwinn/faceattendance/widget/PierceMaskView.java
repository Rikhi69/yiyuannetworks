package com.aiwinn.faceattendance.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aiwinn.base.log.LogUtils;
import com.aiwinn.faceattendance.R;

/**
 * com.aiwinn.faceattendance.widget
 * 2017/09/18
 * Created by LeoLiu on User.
 */

@SuppressLint("AppCompatCustomView")
public class PierceMaskView extends ImageView {

    private Bitmap mSrcRect;
    private Bitmap mDstCircle;

    private int mScreenWidth;
    private int mScreenHeight;

    private int mPiercedX, mPiercedY;

    private int mPiercedRadius;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;

    public PierceMaskView(Context context) {
        this(context, null);
    }

    public PierceMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        if (mScreenWidth == 0) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
        }
        mPaint = new Paint();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    /**
     * @param mPiercedX      镂空的圆心坐标
     * @param mPiercedY      镂空的圆心坐标
     * @param mPiercedRadius 镂空的圆半径
     */
    public void setPiercePosition(int mPiercedX, int mPiercedY, int mPiercedRadius) {
        this.mPiercedX = mPiercedX;
        this.mPiercedY = mPiercedY;
        this.mPiercedRadius = mPiercedRadius;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onDraw(Canvas canvas) {
        LogUtils.e("onDraw", "on draw is called");
        mSrcRect = makeSrcRect();
        mDstCircle = makeDstCircle();
        mPaint.setFilterBitmap(false);
        canvas.saveLayer(0, 0, mScreenWidth, mScreenHeight, null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawBitmap(mDstCircle, 0, 0, mPaint);
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setAlpha(255);
        canvas.drawBitmap(mSrcRect, 0, 0, mPaint);

    }

    /**
     * 创建镂空层圆形形状
     *
     * @return
     */
    private Bitmap makeDstCircle() {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvcs.drawCircle(mPiercedX, mPiercedY, mPiercedRadius, paint);
        return bm;
    }

    /**
     * 创建遮罩层形状
     *
     * @return
     */
    private Bitmap makeSrcRect() {
        Bitmap bm = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.tran_bg));
        canvcs.drawRect(new RectF(0, 0, mScreenWidth, mScreenHeight), paint);
        return bm;
    }

}
