package com.yiyuan.aiwinn.faceattendance.ui.m;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;
import com.yiyuan.ai.R;
import com.yiyuan.aiwinn.faceattendance.AttApp;
import com.yiyuan.aiwinn.faceattendance.common.AttConstants;

import java.util.ArrayList;

/**
 * com.aiwinn.faceattendance.ui
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class ConfigActivity extends BaseActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener,View.OnLongClickListener{

    TextView mTitle;
    ImageView mBack;

    Switch mLR_switch;
    Switch mTB_switch;
    Switch mLiveness_switch;
    Switch mRecognition_switch;

    EditText mCamera_id;
    EditText mCamera_rotate;
    EditText mPreview_rotate;
    TextView mSetting;

    EditText mUnlock;
    TextView mSetting_unlock;

    EditText mLivenes;
    TextView mSetting_livenes;
    EditText mLivenes2;
    EditText mLivenes3;
    EditText mLiveCount;
    EditText mFakeCount;

    Spinner mPreviewSpinner;
    Spinner mRecognitionSpinner;

    RadioGroup mRgRegister;
    RadioGroup mRgDetect;

    ArrayAdapter<String> mPreviewAdapter;
    ArrayList<String> previewList = new ArrayList<>();
    ArrayAdapter<String> mRecognitionModeAdapter;
    ArrayList<String> recognitionModeList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_config;
    }

    @Override
    public void initViews() {
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView)findViewById(R.id.title);

        mRgRegister = (RadioGroup) findViewById(R.id.rgregister);
        mRgDetect = (RadioGroup)findViewById(R.id.rgdetect);

        mPreviewSpinner = (Spinner) findViewById(R.id.previewsp);
        mRecognitionSpinner = (Spinner) findViewById(R.id.recognitionsp);

        mLR_switch = (Switch) findViewById(R.id.lr_switch);
        mTB_switch = (Switch) findViewById(R.id.tb_switch);
        mLiveness_switch = (Switch) findViewById(R.id.liveness_switch);
        mRecognition_switch = (Switch) findViewById(R.id.recognition_switch);

        mCamera_id = (EditText) findViewById(R.id.camera_id);
        mCamera_rotate = (EditText) findViewById(R.id.camera_rotate);
        mPreview_rotate = (EditText) findViewById(R.id.preview_rotate);
        mSetting = (TextView) findViewById(R.id.setting);

        mUnlock = (EditText) findViewById(R.id.unlock);
        mSetting_unlock = (TextView) findViewById(R.id.setting_unlock);

        mLivenes = (EditText) findViewById(R.id.liveness);
        mLivenes2 = (EditText) findViewById(R.id.liveness2);
        mLivenes3 = (EditText) findViewById(R.id.liveness3);
        mLiveCount = (EditText) findViewById(R.id.livecount);
        mFakeCount = (EditText) findViewById(R.id.fakecount);
        mSetting_livenes = (TextView) findViewById(R.id.setting_livenes);

    }

    @Override
    public void initData() {
        mTitle.setText(getResources().getString(R.string.config));

        mLR_switch.setChecked(AttConstants.LEFT_RIGHT);
        mTB_switch.setChecked(AttConstants.TOP_BOTTOM);
        mLiveness_switch.setChecked(ConfigLib.detectWithLiveness);
        mRecognition_switch.setChecked(ConfigLib.detectWithRecognition);

        mCamera_id.setText(String.valueOf(AttConstants.CAMERA_ID));
        mCamera_rotate.setText(String.valueOf(AttConstants.CAMERA_DEGREE));
        mPreview_rotate.setText(String.valueOf(AttConstants.PREVIEW_DEGREE));
        mUnlock.setText(String.valueOf(ConfigLib.featureThreshold));
        mLivenes.setText(String.valueOf(ConfigLib.livenessThreshold));
        mLivenes2.setText(String.valueOf(ConfigLib.livenessThreshold2));
        mLivenes3.setText(String.valueOf(ConfigLib.livenessThreshold3));
        mLiveCount.setText(String.valueOf(ConfigLib.livenessLiveNum));
        mFakeCount.setText(String.valueOf(ConfigLib.livenessFakeNum));

        if (AttConstants.REGISTER_DEFAULT) {
            mRgRegister.check(R.id.register_default);
        }else {
            mRgRegister.check(R.id.register_ex);
        }
        if (AttConstants.DETECT_DEFAULT) {
            mRgDetect.check(R.id.detect_default);
        }else {
            mRgDetect.check(R.id.detect_ex);
        }

        recognitionModeList.clear();
        recognitionModeList.add(getResources().getString(R.string.recognition_mode_track1));
        recognitionModeList.add(getResources().getString(R.string.recognition_mode_track2));
        recognitionModeList.add(getResources().getString(R.string.recognition_mode_recognition));
        mRecognitionModeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, recognitionModeList);
        mRecognitionModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecognitionSpinner.setAdapter(mRecognitionModeAdapter);
        mRecognitionSpinner.setSelection(Constants.RECOGNITION_MODE);
        previewList.clear();
        previewList.add("("+getResources().getString(R.string.auto)+")");
        previewList.add("(1920*1080)");
        previewList.add("(1280*720)");
        previewList.add("(640*480)");
        mPreviewAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, previewList);
        mPreviewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreviewSpinner.setAdapter(mPreviewAdapter);
        switch (AttConstants.CAMERA_PREVIEW_HEIGHT){
            case 0:
                mPreviewSpinner.setSelection(0);
                break;
            case 1080:
                mPreviewSpinner.setSelection(1);
                break;
            case 720:
                mPreviewSpinner.setSelection(2);
                break;
            case 480:
                mPreviewSpinner.setSelection(3);
                break;
        }
    }

    @Override
    public void initListeners() {

        mBack.setOnLongClickListener(this);
        mTitle.setOnLongClickListener(this);
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);

        mSetting.setOnClickListener(this);
        mSetting_unlock.setOnClickListener(this);
        mSetting_livenes.setOnClickListener(this);

        mLiveness_switch.setOnCheckedChangeListener(this);
        mRecognition_switch.setOnCheckedChangeListener(this);
        mLR_switch.setOnCheckedChangeListener(this);
        mTB_switch.setOnCheckedChangeListener(this);

        mPreviewSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        AttConstants.CAMERA_PREVIEW_WIDTH = 0;
                        AttConstants.CAMERA_PREVIEW_HEIGHT = 0;
                        break;

                    case 1:
                        AttConstants.CAMERA_PREVIEW_WIDTH = 1920;
                        AttConstants.CAMERA_PREVIEW_HEIGHT = 1080;
                        break;

                    case 2:
                        AttConstants.CAMERA_PREVIEW_WIDTH = 1280;
                        AttConstants.CAMERA_PREVIEW_HEIGHT = 720;
                        break;

                    case 3:
                        AttConstants.CAMERA_PREVIEW_WIDTH = 640;
                        AttConstants.CAMERA_PREVIEW_HEIGHT = 480;
                        break;
                }
                AttApp.sp.edit().putInt(AttConstants.PREFS_CAMERA_PREVIEW_SIZE,AttConstants.CAMERA_PREVIEW_HEIGHT).commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mRecognitionSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        FaceDetectManager.setRecognizeMode(Constants.RECOGNITION_MODE_TRACKER1);
                        break;

                    case 1:
                        FaceDetectManager.setRecognizeMode(Constants.RECOGNITION_MODE_TRACKER2);
                        break;

                    case 2:
                        FaceDetectManager.setRecognizeMode(Constants.RECOGNITION_MODE_RECOGNITION);
                        break;
                }
                AttApp.sp.edit().putInt(AttConstants.PREFS_TRACKER_MODE,Constants.RECOGNITION_MODE).commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mRgRegister.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.register_default:
                        AttConstants.REGISTER_DEFAULT = true;
                        break;

                    case R.id.register_ex:
                        AttConstants.REGISTER_DEFAULT = false;
                        break;

                }
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_REGISTER_DEFAULT,AttConstants.REGISTER_DEFAULT).commit();
            }
        });

        mRgDetect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){

                    case R.id.detect_default:
                        AttConstants.DETECT_DEFAULT = true;
                        break;

                    case R.id.detect_ex:
                        AttConstants.DETECT_DEFAULT = false;
                        break;

                }
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_DETECT_DEFAULT,AttConstants.DETECT_DEFAULT).commit();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){

            case R.id.recognition_switch:
                ConfigLib.detectWithRecognition = isChecked;
                mRecognition_switch.setChecked(ConfigLib.detectWithRecognition);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_RECOGNITION,ConfigLib.detectWithRecognition).commit();
                break;

            case R.id.liveness_switch:
                ConfigLib.detectWithLiveness = isChecked;
                mLiveness_switch.setChecked(ConfigLib.detectWithLiveness);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_LIVENESS,ConfigLib.detectWithLiveness).commit();
                break;

            case R.id.lr_switch:
                AttConstants.LEFT_RIGHT = isChecked;
                mLR_switch.setChecked(AttConstants.LEFT_RIGHT);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_LR,AttConstants.LEFT_RIGHT).commit();
                break;

            case R.id.tb_switch:
                AttConstants.TOP_BOTTOM = isChecked;
                mTB_switch.setChecked(AttConstants.TOP_BOTTOM);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_TB,AttConstants.TOP_BOTTOM).commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back:
            case R.id.title:
                ConfigActivity.this.finish();
                break;

            case R.id.setting:
                String id = mCamera_id.getText().toString().trim();
                int intId = Integer.parseInt(id);
                String rot = mCamera_rotate.getText().toString().trim();
                int intRot = Integer.parseInt(rot);
                String prot = mPreview_rotate.getText().toString().trim();
                int intPRot = Integer.parseInt(prot);
                AttConstants.CAMERA_ID = intId;
                AttConstants.CAMERA_DEGREE = intRot;
                AttConstants.PREVIEW_DEGREE = intPRot;
                setSucc(AttConstants.CAMERA_ID +" | "+AttConstants.CAMERA_DEGREE +" | "+AttConstants.PREVIEW_DEGREE);
                AttApp.sp.edit().putInt(AttConstants.PREFS_CAMERA_ID,AttConstants.CAMERA_ID).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_CAMERA_DEGREE,AttConstants.CAMERA_DEGREE).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_PREVIEW_DEGREE,AttConstants.PREVIEW_DEGREE).commit();
                break;

            case R.id.setting_unlock:
                String unlock = mUnlock.getText().toString().trim();
                ConfigLib.featureThreshold = Float.valueOf(unlock);
                setSucc(ConfigLib.featureThreshold);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_UNLOCK,ConfigLib.featureThreshold).commit();
                break;

            case R.id.setting_livenes:
                String liveness = mLivenes.getText().toString().trim();
                ConfigLib.livenessThreshold = Float.valueOf(liveness);
                String liveness2 = mLivenes2.getText().toString().trim();
                ConfigLib.livenessThreshold2 = Float.valueOf(liveness2);
                String liveness3 = mLivenes3.getText().toString().trim();
                ConfigLib.livenessThreshold3 = Float.valueOf(liveness3);
                String lives = mLiveCount.getText().toString().trim();
                ConfigLib.livenessLiveNum = Integer.parseInt(lives);
                String fakes = mFakeCount.getText().toString().trim();
                ConfigLib.livenessFakeNum = Integer.parseInt(fakes);
                setSucc(ConfigLib.livenessThreshold+" | "+ConfigLib.livenessThreshold2+" | "+ConfigLib.livenessThreshold3 +" | "+ConfigLib.livenessLiveNum+" | "+ConfigLib.livenessFakeNum);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_LIVENESST,ConfigLib.livenessThreshold).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_LIVENESST2,ConfigLib.livenessThreshold2).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_LIVENESST3,ConfigLib.livenessThreshold3).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_LIVECOUNT,ConfigLib.livenessLiveNum).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_FAKECOUNT,ConfigLib.livenessFakeNum).commit();
                break;

            default:break;
        }
    }

    void setSucc(Object o){
        ToastUtils.showLong(getResources().getString(R.string.set_success)+" : "+o);
        ConfigActivity.this.finish();
    }

    void setFail(){
        ToastUtils.showLong(getResources().getString(R.string.set_fail));
    }

    @Override
    public boolean onLongClick(View v) {

        if(v.getId() == R.id.back || v.getId() == R.id.title){
            mIntent = new Intent(ConfigActivity.this,DebugActivity.class);
            startActivity(mIntent);
            return true;
        }

        return false;
    }
}
