package com.lxl.shop.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxl.shop.R;


/**
 * Created by xiangleiliu on 2017/8/24.
 */
public class StockTitleView extends RelativeLayout {

    Context mContext;
    TextView mTitle;
    ImageView mBackBtn;
    TextView mActionBtn;


    public StockTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StockTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StockTitleView);
        String title = a.getString(R.styleable.StockTitleView_stock_title_text_msg);
        int titleStyle = a.getResourceId(R.styleable.StockTitleView_stock_title_text_style, R.style.text_18_ffffff);
        a.recycle();
        inflate(context, R.layout.shop_widget_basetitle_view, this);
        initView();
        setTitle(title, titleStyle);
        initListener();
    }

    private void initListener() {
        mBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!(mContext instanceof Activity)) {
                    return;
                }
//                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);

//                ((Activity) mContext).onKeyDown(KeyEvent.KEYCODE_BACK, keyEvent);
                ((Activity) mContext).finish();
            }
        });

    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.title_view);
        mBackBtn = (ImageView) findViewById(R.id.back_btn);
        mActionBtn = (TextView) findViewById(R.id.action_btn);
    }

    public void setTitle(CharSequence title) {
        if (title instanceof String) {
            setTitle(title, R.style.text_18_ffffff);
        } else {
            mTitle.setText(title);
        }

    }

    public void setTitle(CharSequence title, int style) {
        mTitle.setTextAppearance(mContext, style);
        mTitle.setText(title);
    }

    public TextView getActionBtn() {
        return mActionBtn;
    }

}
