package com.example.administrator.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lxl.shop.InitActivity;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConfig;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.utils.DateUtil;
import com.lxl.shop.utils.IOHelper;

import java.io.File;
import java.util.UUID;

/**
 * @author yanglei
 * @date 2018/12/1
 */

public class MainActivity3 extends InitActivity implements View.OnClickListener, Dllipcsdk.CBJpgData {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    Handler handler = new Handler();
    EditText ipText;
    Button startBtn;
    Button stopBtn;
    TextView showText;

    long lRawData = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page_test);
        initView();
        initData();
        initListeners();
        ipText.setText(ShopConfig.strIp);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initView() {
        ipText = findViewById(R.id.iptext);
        startBtn = findViewById(R.id.start_btn);
        stopBtn = findViewById(R.id.stop_btn);
        showText = findViewById(R.id.show_text);
        showText.setMovementMethod(ScrollingMovementMethod.getInstance());
        showText.setText(stringFromJNITest());
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
//        postShowText();
    }

    public void postShowText() {
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                double random = Math.random();
                if (random > 0.5) {
                    showText.append("aaaaa\n");
                } else {
                    showText.append("xxxxx\n");
                }
                postShowText();
            }
        }, 1000);
    }

    @Override
    protected void initListeners() {
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_btn) {
            String strIp = ipText.getText().toString();
            if (lRawData != -1) {
                Dllipcsdk.IPCNET_StopRawData(lRawData);
                lRawData = -1;
            }
            lRawData = Dllipcsdk.IPCNET_StartJpgData(strIp, ShopConfig.PORT, ShopConfig.USER_NAME, ShopConfig.PASS_WORD, this);
        } else if (id == R.id.stop_btn) {
            if (lRawData != -1) {
                Dllipcsdk.IPCNET_StopRawData(lRawData);
                lRawData = -1;
            }
        }
    }

    @Override
    public void JpgData(int lJpgHandle, int nErrorType, int nErrorCode, byte[] pJpgBuffer, int lJpgBufSize) {

        //生成缓存图片，放在temp文件加下面

        String s = DateUtil.getCurrentTime() + UUID.randomUUID().toString();
        String filePath = ShopConstants.TEMP_PATH + File.separator + s + ".jpg";
        final String str = "lJpgBufSize:" + lJpgBufSize + ",savePath:" + filePath;
        IOHelper.byte2image(pJpgBuffer, filePath);
        handler.post(new Runnable() {
            @Override
            public void run() {
                showText.append(str);
                int offset = showText.getLineCount() * showText.getLineHeight();
                if (offset > showText.getHeight()) {
                    showText.scrollTo(0, offset - showText.getHeight());
                }
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String stringFromJNITest();

}
