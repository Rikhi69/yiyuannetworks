package com.aiwinn.faceattendance.ui.m;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.aiwinn.base.AiwinnManager;
import com.aiwinn.base.activity.BaseActivity;
import com.aiwinn.base.util.ToastUtils;
import com.aiwinn.faceattendance.AttApp;
import com.aiwinn.faceattendance.R;
import com.aiwinn.faceattendance.common.AttConstants;
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;

import java.util.ArrayList;

/**
 * com.aiwinn.faceattendance.ui
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class ConfigActivity extends BaseActivity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener{

    TextView mTitle;
    ImageView mBack;

    Switch mLiveness_switch;
    Switch mRecognition_switch;
    Switch mSaveBlur_switch;
    Switch mSaveNoFace_switch;
    Switch mSaveSS_switch;

    Switch mLR_switch;
    Switch mTB_switch;

    EditText mCamera_id;
    EditText mCamera_rotate;
    EditText mPreview_rotate;
    TextView mSetting;

    EditText mDetectRate;
    EditText mDetectSize;
    EditText mTrackerSize;
    EditText mFeatureSize;
    TextView mSettingDetect;

    EditText mUnlock;
    TextView mSetting_unlock;
    EditText mLivenes;
    TextView mSetting_livenes;
    EditText mFaceMinima;
    TextView mSetting_faceMinima;

    Spinner mPreviewSpinner;
    Spinner mTrackerSpinner;
    Spinner mDetectSpinner;

    EditText mRegisterLightMin;
    EditText mRegisterLightMax;
    EditText mRegisterBlur;
    TextView mRegister_Settings_light_blur;

    EditText mDetectLightMin;
    EditText mDetectLightMax;
    EditText mDetectBlur;
    EditText mDetectBlurNew;
    TextView mDetect_Settings_light_blur;

    Switch mDebug_switch;
    RelativeLayout mRSb;
    View mRSbv;
    RelativeLayout mRSn;
    View mRSnv;
    RelativeLayout mRSs;
    View mRSsv;

    ArrayAdapter<String> mPreviewAdapter;
    ArrayList<String> previewList = new ArrayList<>();
    ArrayAdapter<String> mTrackerModeAdapter;
    ArrayList<String> trackerModeList = new ArrayList<>();
    ArrayAdapter<String> mDetectModeAdapter;
    ArrayList<String> detectModeList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_config;
    }

    @Override
    public void initViews() {
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView)findViewById(R.id.title);

        mPreviewSpinner = (Spinner) findViewById(R.id.previewsp);
        mTrackerSpinner = (Spinner) findViewById(R.id.trackersp);
        mDetectSpinner = (Spinner) findViewById(R.id.detectsp);

        mLiveness_switch = (Switch) findViewById(R.id.liveness_switch);
        mRecognition_switch = (Switch) findViewById(R.id.recognition_switch);
        mSaveBlur_switch = (Switch) findViewById(R.id.saveblur_switch);
        mSaveNoFace_switch = (Switch) findViewById(R.id.savenoface_switch);
        mSaveSS_switch = (Switch) findViewById(R.id.savess_switch);

        mLR_switch = (Switch) findViewById(R.id.lr_switch);
        mTB_switch = (Switch) findViewById(R.id.tb_switch);

        mCamera_id = (EditText) findViewById(R.id.camera_id);
        mCamera_rotate = (EditText) findViewById(R.id.camera_rotate);
        mPreview_rotate = (EditText) findViewById(R.id.preview_rotate);
        mSetting = (TextView) findViewById(R.id.setting);

        mDetectRate = (EditText) findViewById(R.id.detect_rate);
        mDetectSize = (EditText) findViewById(R.id.detect_size);
        mTrackerSize = (EditText) findViewById(R.id.tracker_size);
        mFeatureSize = (EditText) findViewById(R.id.feature_size);
        mSettingDetect = (TextView) findViewById(R.id.setting_detect);

        mUnlock = (EditText) findViewById(R.id.unlock);
        mSetting_unlock = (TextView) findViewById(R.id.setting_unlock);

        mLivenes = (EditText) findViewById(R.id.liveness);
        mSetting_livenes = (TextView) findViewById(R.id.setting_livenes);

        mFaceMinima = (EditText) findViewById(R.id.faceregister);
        mSetting_faceMinima = (TextView) findViewById(R.id.setting_faceregister);

        mRegisterLightMin = (EditText) findViewById(R.id.registerlightMin);
        mRegisterLightMax = (EditText) findViewById(R.id.registerlightMax);
        mRegisterBlur = (EditText) findViewById(R.id.registerblur);
        mRegister_Settings_light_blur = (TextView) findViewById(R.id.settings_register_light_blur);

        mDetectLightMin = (EditText) findViewById(R.id.detectlightMin);
        mDetectLightMax = (EditText) findViewById(R.id.detectlightMax);
        mDetectBlur = (EditText) findViewById(R.id.detectblur);
        mDetectBlurNew = (EditText) findViewById(R.id.detectblurnew);
        mDetect_Settings_light_blur = (TextView) findViewById(R.id.settings_detect_light_blur);

        mDebug_switch = (Switch) findViewById(R.id.debug_switch);
        mRSb = (RelativeLayout) findViewById(R.id.rsb);
        mRSbv = (View) findViewById(R.id.rsbv);
        mRSn = (RelativeLayout) findViewById(R.id.rsn);
        mRSnv = (View) findViewById(R.id.rsnv);
        mRSs = (RelativeLayout) findViewById(R.id.rss);
        mRSsv = (View) findViewById(R.id.rssv);
    }

    @Override
    public void initData() {
        mTitle.setText(getResources().getString(R.string.config));
        mLiveness_switch.setChecked(ConfigLib.detectWithLiveness);
        mRecognition_switch.setChecked(ConfigLib.detectWithRecognition);
        mSaveBlur_switch.setChecked(Constants.DEBUG_SAVE_BLUR);
        mSaveNoFace_switch.setChecked(Constants.DEBUG_SAVE_NOFACE);
        mSaveSS_switch.setChecked(Constants.DEBUG_SAVE_SIMILARITY_SMALL);
        mLR_switch.setChecked(AttConstants.LEFT_RIGHT);
        mTB_switch.setChecked(AttConstants.TOP_BOTTOM);
        mCamera_id.setText(String.valueOf(AttConstants.CAMERA_ID));
        mCamera_rotate.setText(String.valueOf(AttConstants.CAMERA_DEGREE));
        mPreview_rotate.setText(String.valueOf(AttConstants.PREVIEW_DEGREE));
        mDetectRate.setText(String.valueOf(ConfigLib.picScaleRate));
        mDetectSize.setText(String.valueOf(FaceDetectManager.getFaceMinRect()));
        mTrackerSize.setText(String.valueOf(ConfigLib.picScaleSize));
        mUnlock.setText(String.valueOf(ConfigLib.featureThreshold));
        mLivenes.setText(String.valueOf(ConfigLib.livenessThreshold));
        mFaceMinima.setText(String.valueOf(ConfigLib.registerPicRect));
        mRegisterLightMax.setText(String.valueOf(ConfigLib.maxRegisterBrightness));
        mRegisterLightMin.setText(String.valueOf(ConfigLib.minRegisterBrightness));
        mRegisterBlur.setText(String.valueOf(ConfigLib.blurRegisterThreshold));
        mDetectLightMax.setText(String.valueOf(ConfigLib.maxRecognizeBrightness));
        mDetectLightMin.setText(String.valueOf(ConfigLib.minRecognizeBrightness));
        mDetectBlur.setText(String.valueOf(ConfigLib.blurRecognizeThreshold));
        mDetectBlurNew.setText(String.valueOf(ConfigLib.blurRecognizeNewThreshold));
        mFeatureSize.setText(String.valueOf(ConfigLib.Nv21ToBitmapScale));
        mDebug_switch.setChecked(AttConstants.DEBUG);
        refreshDebug();
        trackerModeList.clear();
        trackerModeList.add(getResources().getString(R.string.detect_mode_track));
        trackerModeList.add(getResources().getString(R.string.detect_mode_gaa));
        trackerModeList.add(getResources().getString(R.string.detect_mode_sm));
        mTrackerModeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, trackerModeList);
        mTrackerModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrackerSpinner.setAdapter(mTrackerModeAdapter);
        mTrackerSpinner.setSelection(Constants.TRACKER_MODE);
        detectModeList.clear();
        detectModeList.add("0");
        detectModeList.add("1");
        detectModeList.add("2");
        detectModeList.add("3");
        detectModeList.add("4");
        mDetectModeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, detectModeList);
        mDetectModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDetectSpinner.setAdapter(mDetectModeAdapter);
        mDetectSpinner.setSelection(FaceDetectManager.getDetectFaceMode());
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

    private void refreshDebug() {
        if (AttConstants.DEBUG) {
            mRSb.setVisibility(View.VISIBLE);
            mRSbv.setVisibility(View.VISIBLE);
            mRSn.setVisibility(View.VISIBLE);
            mRSnv.setVisibility(View.VISIBLE);
            mRSs.setVisibility(View.VISIBLE);
            mRSsv.setVisibility(View.VISIBLE);
        }else {
            mRSb.setVisibility(View.GONE);
            mRSbv.setVisibility(View.GONE);
            mRSn.setVisibility(View.GONE);
            mRSnv.setVisibility(View.GONE);
            mRSs.setVisibility(View.GONE);
            mRSsv.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListeners() {

        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);

        mSetting.setOnClickListener(this);
        mSettingDetect.setOnClickListener(this);
        mSetting_unlock.setOnClickListener(this);
        mSetting_livenes.setOnClickListener(this);
        mSetting_faceMinima.setOnClickListener(this);
        mRegister_Settings_light_blur.setOnClickListener(this);
        mDetect_Settings_light_blur.setOnClickListener(this);

        mLiveness_switch.setOnCheckedChangeListener(this);
        mRecognition_switch.setOnCheckedChangeListener(this);
        mSaveBlur_switch.setOnCheckedChangeListener(this);
        mSaveNoFace_switch.setOnCheckedChangeListener(this);
        mSaveSS_switch.setOnCheckedChangeListener(this);
        mDebug_switch.setOnCheckedChangeListener(this);
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

        mTrackerSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        FaceDetectManager.setRecognizeMode(Constants.TRACKER_MODE_TRACKER);
                        break;

                    case 1:
                        FaceDetectManager.setRecognizeMode(Constants.TRACKER_MODE_GAA);
                        break;

                    case 2:
                        FaceDetectManager.setRecognizeMode(Constants.TRACKER_MODE_SM);
                        break;
                }
                AttApp.sp.edit().putInt(AttConstants.PREFS_TRACKER_MODE,Constants.TRACKER_MODE).commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mDetectSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                FaceDetectManager.setDetectFaceMode(arg2);
                AttApp.sp.edit().putInt(AttConstants.PREFS_DETECT_MODE,FaceDetectManager.getDetectFaceMode()).commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

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
            case R.id.saveblur_switch:
                Constants.DEBUG_SAVE_BLUR = isChecked;
                mSaveBlur_switch.setChecked(Constants.DEBUG_SAVE_BLUR);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_SAVEBLURDATA,Constants.DEBUG_SAVE_BLUR).commit();
                break;

            case R.id.savenoface_switch:
                Constants.DEBUG_SAVE_NOFACE = isChecked;
                mSaveNoFace_switch.setChecked(Constants.DEBUG_SAVE_NOFACE);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_SAVENOFACEDATA,Constants.DEBUG_SAVE_NOFACE).commit();
                break;

            case R.id.savess_switch:
                Constants.DEBUG_SAVE_SIMILARITY_SMALL = isChecked;
                mSaveSS_switch.setChecked(Constants.DEBUG_SAVE_SIMILARITY_SMALL);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_SAVESSDATA,Constants.DEBUG_SAVE_SIMILARITY_SMALL).commit();
                break;

            case R.id.debug_switch:
                AttConstants.DEBUG = isChecked;
                mDebug_switch.setChecked(AttConstants.DEBUG);
                refreshDebug();
                FaceDetectManager.setDebug(AttConstants.DEBUG);
                AiwinnManager.getInstance().setDebug(AttConstants.DEBUG);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_DEBUG,AttConstants.DEBUG).commit();
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

            case R.id.setting_detect:
                String rate = mDetectRate.getText().toString().trim();
                float floatRate = Float.parseFloat(rate);
                String size = mTrackerSize.getText().toString().trim();
                float floatSize = Float.parseFloat(size);
                String iSize = mDetectSize.getText().toString().trim();
                int intSize = Integer.parseInt(iSize);
                String iFSize = mFeatureSize.getText().toString().trim();
                int intFSize = Integer.parseInt(iFSize);
                ConfigLib.picScaleRate = floatRate;
                ConfigLib.picScaleSize = floatSize;
                ConfigLib.Nv21ToBitmapScale = intFSize;
                FaceDetectManager.setFaceMinRect(intSize);
                setSucc(ConfigLib.picScaleRate +" | "+ConfigLib.picScaleSize+" | "+FaceDetectManager.getFaceMinRect());
                AttApp.sp.edit().putFloat(AttConstants.PREFS_DETECT_RATE,ConfigLib.picScaleRate).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_DETECT_SIZE,FaceDetectManager.getFaceMinRect()).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_TRACKER_SIZE,ConfigLib.picScaleSize).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_FEATURE_SIZE,ConfigLib.Nv21ToBitmapScale).commit();
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
                setSucc(ConfigLib.livenessThreshold);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_LIVENESST,ConfigLib.livenessThreshold).commit();
                break;

            case R.id.setting_faceregister:
                String faceminima = mFaceMinima.getText().toString().trim();
                ConfigLib.registerPicRect = Integer.parseInt(faceminima);
                setSucc(ConfigLib.registerPicRect);
                AttApp.sp.edit().putInt(AttConstants.PREFS_FACEMINIMA,ConfigLib.registerPicRect).commit();
                break;


            case R.id.settings_register_light_blur:
                String registermaxlight = mRegisterLightMax.getText().toString().trim();
                ConfigLib.maxRegisterBrightness = Float.valueOf(registermaxlight);
                String registerminlight = mRegisterLightMin.getText().toString().trim();
                ConfigLib.minRegisterBrightness = Float.valueOf(registerminlight);
                String registerblur = mRegisterBlur.getText().toString().trim();
                ConfigLib.blurRegisterThreshold = Float.valueOf(registerblur);
                setSucc(ConfigLib.maxRegisterBrightness +" | "+ ConfigLib.minRegisterBrightness +" | "+ ConfigLib.blurRegisterThreshold);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MAXREGISTERBRIGHTNESS,ConfigLib.maxRegisterBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MINREGISTERBRIGHTNESS,ConfigLib.minRegisterBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_BLURREGISTERTHRESHOLD,ConfigLib.blurRegisterThreshold).commit();
                break;

            case R.id.settings_detect_light_blur:
                String detectmaxlight = mDetectLightMax.getText().toString().trim();
                ConfigLib.maxRecognizeBrightness = Float.valueOf(detectmaxlight);
                String detectminlight = mDetectLightMin.getText().toString().trim();
                ConfigLib.minRecognizeBrightness = Float.valueOf(detectminlight);
                String detectblur = mDetectBlur.getText().toString().trim();
                ConfigLib.blurRecognizeThreshold = Float.valueOf(detectblur);
                String detectblurnew = mDetectBlurNew.getText().toString().trim();
                ConfigLib.blurRecognizeNewThreshold = Float.valueOf(detectblurnew);
                setSucc(ConfigLib.maxRecognizeBrightness +" | "+ ConfigLib.minRecognizeBrightness +" | "+ ConfigLib.blurRecognizeThreshold+" | "+ConfigLib.blurRecognizeNewThreshold);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MAXRECOGNIZEBRIGHTNESS,ConfigLib.maxRecognizeBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MINRECOGNIZEBRIGHTNESS,ConfigLib.minRecognizeBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_BLURECOGNIZETHRESHOLD,ConfigLib.blurRecognizeThreshold).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_BLURECOGNIZENEWTHRESHOLD,ConfigLib.blurRecognizeNewThreshold).commit();
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

}
