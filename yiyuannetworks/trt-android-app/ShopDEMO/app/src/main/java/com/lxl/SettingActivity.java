package com.lxl;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.lxl.shop.InitActivity;
import com.lxl.shop.R;
import com.lxl.shop.common.ShopConfig;
import com.lxl.shop.common.ShopConstants;
import com.lxl.shop.utils.IOHelper;
import com.lxl.shop.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends InitActivity implements View.OnClickListener {

    EditText editTextUrl;

    RadioButton radioButtonMobile;
    RadioButton radioButtonPad;

    EditText editTextCount;

    CheckBox checkBoxCamera;
    CheckBox checkBoxSDCard;
    CheckBox checkBoxVideo;

    List<RadioButton> firstRadioList = new ArrayList<>();
    List<CheckBox> supportCheckList = new ArrayList<>();

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!ShopConfig.isPhone){
            if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initAction();
        initData();
        initListeners();
    }

    @Override
    protected void initView() {
        editTextUrl = findViewById(R.id.et_url);
        radioButtonMobile = findViewById(R.id.plat_mobile);
        radioButtonPad = findViewById(R.id.plat_pad);
        editTextCount = findViewById(R.id.et_count);
        checkBoxCamera = findViewById(R.id.cb_camera);
        checkBoxSDCard = findViewById(R.id.cb_sdcard);
        checkBoxVideo = findViewById(R.id.cb_video);

        firstRadioList.add(radioButtonMobile);
        firstRadioList.add(radioButtonPad);

        supportCheckList.add(checkBoxCamera);
        supportCheckList.add(checkBoxSDCard);
        supportCheckList.add(checkBoxVideo);


    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        Map<String,String> dataMap = loadConfig();
        for(String key : dataMap.keySet()){
            if ("url".equals(key)) {
                editTextUrl.setText(dataMap.get(key));
            } else if ("first".equals(key)) {
                for(RadioButton radioButtonTemp : firstRadioList){
                    if(radioButtonTemp.getTag().equals(dataMap.get(key))){
                        radioButtonTemp.setChecked(true);
                    }
                }
            }else if ("count".equals(key)) {
                editTextCount.setText(dataMap.get(key));
            }else if ("support".equals(key)) {
                String support = dataMap.get(key);
                if(StringUtil.isNotBlank(support)){
                    for(String str : support.split(",")){
                        for(CheckBox checkBoxTemp : supportCheckList){
                            if(checkBoxTemp.getTag().equals(str)){
                                checkBoxTemp.setChecked(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.finish).setOnClickListener(this);
    }


    private Map<String,String> loadConfig(){
        Map<String,String> dataMap = new HashMap<>();
        file = new File(ShopConstants.CONFIG_PATH);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        File tempFile = new File(ShopConstants.TEMP_PATH);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }

        if (file.exists()) {
            //读取配置文件
            InputStream inputStream = IOHelper.fromFileToIputStream(file);//权限问题
            List<String> strings = IOHelper.readListStrByCode(inputStream, "utf-8");
            for (String str : strings) {
                if (StringUtil.emptyOrNull(str) || !str.contains("=")) {
                    continue;
                }
                String[] split = str.split("=");
                String key = split[0];
                String value = split[1];
                dataMap.put(key,value);
            }
        }
        return dataMap;
    }

    public void updateConfig() throws IOException {
        if(!file.exists()){
            file.createNewFile();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("url="+ editTextUrl.getText().toString());
        builder.append("\n");
        for(RadioButton radioButtonTemp : firstRadioList){
            if(radioButtonTemp.isChecked()){
                builder.append("first="+radioButtonTemp.getTag().toString());
            }
        }
        builder.append("\n");
        builder.append("count="+editTextCount.getText().toString());
        builder.append("\n");
        builder.append("support=");
        for(CheckBox checkBoxTemp : supportCheckList){
            if(checkBoxTemp.isChecked()){
                builder.append(checkBoxTemp.getTag().toString()+",");
            }
        }
        builder.delete(builder.length()-1,builder.length());
        IOHelper.writerStrByCodeToFile(file, "utf-8", false, builder.toString());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        }else if (id == R.id.finish) {
            try {
                updateConfig();
                Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
