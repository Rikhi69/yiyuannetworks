package com.yiyuan.aiwinn.faceattendance.ui.m;

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
import com.aiwinn.facedetectsdk.FaceDetectManager;
import com.aiwinn.facedetectsdk.common.ConfigLib;
import com.aiwinn.facedetectsdk.common.Constants;
import com.yiyuan.ai.R;
import com.yiyuan.aiwinn.faceattendance.AttApp;
import com.yiyuan.aiwinn.faceattendance.common.AttConstants;

import java.util.ArrayList;

/**
 * com.aiwinn.faceattendance.ui.m
 * SDK_ATT
 * 2018/12/20
 * Created by LeoLiu on User
 */

public class DebugActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    TextView mTitle;
    ImageView mBack;

    Spinner mDetectSpinner;
    Spinner mLiveSpinner;

    Switch mDebug_switch;
    RelativeLayout mRSb;
    View mRSbv;
    RelativeLayout mRSn;
    View mRSnv;
    RelativeLayout mRSs;
    View mRSsv;
    RelativeLayout mRSf;
    View mRSfv;
    RelativeLayout mRSl;
    View mRSlv;
    RelativeLayout mRSt;
    View mRStv;
    RelativeLayout mRStan;
    View mRStanv;

    Switch mSaveBlur_switch;
    Switch mSaveNoFace_switch;
    Switch mSaveSS_switch;
    Switch mSaveF_switch;
    Switch mSaveL_switch;
    Switch mSaveTracker_switch;
    Switch mSaveTan_switch;

    EditText mRegisterLightMin;
    EditText mRegisterLightMax;
    EditText mRegisterBlur;
    EditText mFaceMinima;
    TextView mRegister_Settings_light_blur;

    EditText mDetectLightMin;
    EditText mDetectLightMax;
    EditText mDetectBlur;
    EditText mDetectBlurNew;
    TextView mDetect_Settings_light_blur;

    EditText mDetectRate;
    EditText mDetectSize;
    EditText mTrackerSize;
    EditText mFeatureSize;
    EditText mTrackerFaceSize;
    TextView mSettingDetect;

    ArrayAdapter<String> mDetectModeAdapter;
    ArrayList<String> detectModeList = new ArrayList<>();
    ArrayAdapter<String> mLiveModeAdapter;
    ArrayList<String> liveModeList = new ArrayList<>();

    Switch mInfrared_switch;
    Switch mSaveInfrared_switch;
    Switch mSaveInfraredLive_switch;
    Switch mSaveInfraredFake_switch;
    EditText mInfraredValue;
    TextView mSetting_infrared;
    Spinner mInfraredSpinner;
    ArrayAdapter<String> mInfraredModeAdapter;
    ArrayList<String> infraredModeList = new ArrayList<>();

    boolean initInfrared = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_debug;
    }

    @Override
    public void initViews() {
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView)findViewById(R.id.title);

        mInfrared_switch = (Switch)findViewById(R.id.infrared_switch);
        mSaveInfrared_switch = (Switch)findViewById(R.id.saveinfrared_switch);
        mSaveInfraredLive_switch = (Switch)findViewById(R.id.saveinfraredlive_switch);
        mSaveInfraredFake_switch = (Switch)findViewById(R.id.saveinfraredfake_switch);
        mInfraredValue = (EditText)findViewById(R.id.infrared);
        mSetting_infrared = (TextView)findViewById(R.id.setting_infrared);
        mInfraredSpinner = (Spinner)findViewById(R.id.infraredsp);

        mDetectSpinner = (Spinner) findViewById(R.id.detectsp);
        mLiveSpinner = (Spinner) findViewById(R.id.livesp);

        mDebug_switch = (Switch) findViewById(R.id.debug_switch);
        mRSb = (RelativeLayout) findViewById(R.id.rsb);
        mRSbv = (View) findViewById(R.id.rsbv);
        mRSn = (RelativeLayout) findViewById(R.id.rsn);
        mRSnv = (View) findViewById(R.id.rsnv);
        mRSs = (RelativeLayout) findViewById(R.id.rss);
        mRSsv = (View) findViewById(R.id.rssv);
        mRSf = (RelativeLayout) findViewById(R.id.rsf);
        mRSfv = (View) findViewById(R.id.rsfv);
        mRSl = (RelativeLayout) findViewById(R.id.rsl);
        mRSlv = (View) findViewById(R.id.rslv);
        mRSt = (RelativeLayout) findViewById(R.id.rst);
        mRStv = (View) findViewById(R.id.rstv);
        mRStan = (RelativeLayout) findViewById(R.id.rstan);
        mRStanv = (View) findViewById(R.id.rstanv);

        mSaveBlur_switch = (Switch) findViewById(R.id.saveblur_switch);
        mSaveNoFace_switch = (Switch) findViewById(R.id.savenoface_switch);
        mSaveSS_switch = (Switch) findViewById(R.id.savess_switch);
        mSaveF_switch = (Switch) findViewById(R.id.savefake_switch);
        mSaveL_switch = (Switch) findViewById(R.id.savelive_switch);
        mSaveTracker_switch = (Switch) findViewById(R.id.savet_switch);
        mSaveTan_switch = (Switch) findViewById(R.id.savetan_switch);

        mRegisterLightMin = (EditText) findViewById(R.id.registerlightMin);
        mRegisterLightMax = (EditText) findViewById(R.id.registerlightMax);
        mRegisterBlur = (EditText) findViewById(R.id.registerblur);
        mFaceMinima = (EditText) findViewById(R.id.faceregister);
        mRegister_Settings_light_blur = (TextView) findViewById(R.id.settings_register_light_blur);

        mDetectLightMin = (EditText) findViewById(R.id.detectlightMin);
        mDetectLightMax = (EditText) findViewById(R.id.detectlightMax);
        mDetectBlur = (EditText) findViewById(R.id.detectblur);
        mDetectBlurNew = (EditText) findViewById(R.id.detectblurnew);
        mDetect_Settings_light_blur = (TextView) findViewById(R.id.settings_detect_light_blur);

        mDetectRate = (EditText) findViewById(R.id.detect_rate);
        mDetectSize = (EditText) findViewById(R.id.detect_size);
        mTrackerSize = (EditText) findViewById(R.id.tracker_size);
        mFeatureSize = (EditText) findViewById(R.id.feature_size);
        mTrackerFaceSize = (EditText) findViewById(R.id.tracker_face_size);
        mSettingDetect = (TextView) findViewById(R.id.setting_detect);
    }

    @Override
    public void initData() {
        mTitle.setText(getResources().getString(R.string.debug));
        initInfrared = true;
        mInfrared_switch.setChecked(ConfigLib.detectWithInfraredLiveness);
        if (ConfigLib.detectWithInfraredLiveness) {
            mSaveInfrared_switch.setChecked(FaceDetectManager.getInfraredDebugPicsState());
        }else {
            mSaveInfrared_switch.setVisibility(View.GONE);
        }
        mSaveInfraredLive_switch.setChecked(Constants.DEBUG_SAVE_INFRARED_LIVE);
        mSaveInfraredFake_switch.setChecked(Constants.DEBUG_SAVE_INFRARED_FAKE);
        mInfraredValue.setText(String.valueOf(ConfigLib.livenessInFraredThreshold));
        infraredModeList.clear();
        infraredModeList.add("940NM");
        infraredModeList.add("850NM");
        mInfraredModeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, infraredModeList);
        mInfraredModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInfraredSpinner.setAdapter(mInfraredModeAdapter);
        mInfraredSpinner.setSelection(Constants.INFRARED_MODE);

        mDebug_switch.setChecked(AttConstants.DEBUG);
        mSaveTan_switch.setChecked(Constants.DEBUG_SAVE_LIVEPIC_SDK);
        mSaveBlur_switch.setChecked(Constants.DEBUG_SAVE_BLUR);
        mSaveNoFace_switch.setChecked(Constants.DEBUG_SAVE_NOFACE);
        mSaveSS_switch.setChecked(Constants.DEBUG_SAVE_SIMILARITY_SMALL);
        mSaveF_switch.setChecked(Constants.DEBUG_SAVE_FAKE);
        mSaveL_switch.setChecked(Constants.DEBUG_SAVE_LIVE);
        mSaveTracker_switch.setChecked(Constants.DEBUG_SAVE_TRACKER);

        refreshDebug();

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
        liveModeList.clear();
        liveModeList.add(getResources().getString(R.string.live_mode_normal));
        liveModeList.add(getResources().getString(R.string.live_mode_auto));
        mLiveModeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, liveModeList);
        mLiveModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLiveSpinner.setAdapter(mLiveModeAdapter);
        mLiveSpinner.setSelection(Constants.LIVENESS_MODE);

        mFaceMinima.setText(String.valueOf(ConfigLib.registerPicRect));
        mRegisterLightMax.setText(String.valueOf(ConfigLib.maxRegisterBrightness));
        mRegisterLightMin.setText(String.valueOf(ConfigLib.minRegisterBrightness));
        mRegisterBlur.setText(String.valueOf(ConfigLib.blurRegisterThreshold));

        mDetectLightMax.setText(String.valueOf(ConfigLib.maxRecognizeBrightness));
        mDetectLightMin.setText(String.valueOf(ConfigLib.minRecognizeBrightness));
        mDetectBlur.setText(String.valueOf(ConfigLib.blurRecognizeThreshold));
        mDetectBlurNew.setText(String.valueOf(ConfigLib.blurRecognizeNewThreshold));

        mDetectRate.setText(String.valueOf(ConfigLib.picScaleRate));
        mDetectSize.setText(String.valueOf(FaceDetectManager.getFaceMinRect()));
        mTrackerSize.setText(String.valueOf(ConfigLib.picScaleSize));
        mFeatureSize.setText(String.valueOf(ConfigLib.Nv21ToBitmapScale));
        mTrackerFaceSize.setText(String.valueOf(ConfigLib.minRecognizeRect));
    }

    @Override
    public void initListeners() {
        mBack.setOnClickListener(this);
        mTitle.setOnClickListener(this);

        mSetting_infrared.setOnClickListener(this);
        mInfrared_switch.setOnCheckedChangeListener(this);
        mSaveInfrared_switch.setOnCheckedChangeListener(this);
        mSaveInfraredLive_switch.setOnCheckedChangeListener(this);
        mSaveInfraredFake_switch.setOnCheckedChangeListener(this);

        mRegister_Settings_light_blur.setOnClickListener(this);
        mDetect_Settings_light_blur.setOnClickListener(this);
        mSettingDetect.setOnClickListener(this);

        mSaveBlur_switch.setOnCheckedChangeListener(this);
        mSaveNoFace_switch.setOnCheckedChangeListener(this);
        mSaveSS_switch.setOnCheckedChangeListener(this);
        mDebug_switch.setOnCheckedChangeListener(this);
        mSaveF_switch.setOnCheckedChangeListener(this);
        mSaveL_switch.setOnCheckedChangeListener(this);
        mSaveTracker_switch.setOnCheckedChangeListener(this);
        mSaveTan_switch.setOnCheckedChangeListener(this);



        mInfraredSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Constants.INFRARED_MODE = arg2;
                AttApp.sp.edit().putInt(AttConstants.PREFS_INFRAREDMODE,Constants.INFRARED_MODE).commit();
                if (ConfigLib.detectWithInfraredLiveness && !initInfrared) {
                    ToastUtils.showShort(getResources().getString(R.string.reopen));
                }
                if (initInfrared) {
                    initInfrared = false;
                }
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

        mLiveSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Constants.LIVENESS_MODE = arg2;
                AttApp.sp.edit().putInt(AttConstants.PREFS_LIVE_MODE,Constants.LIVENESS_MODE).commit();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    void setSucc(Object o){
        ToastUtils.showLong(getResources().getString(R.string.set_success)+" : "+o);
        DebugActivity.this.finish();
    }

    void setFail(){
        ToastUtils.showLong(getResources().getString(R.string.set_fail));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back:
            case R.id.title:
                DebugActivity.this.finish();
                break;

            case R.id.settings_register_light_blur:
                String registermaxlight = mRegisterLightMax.getText().toString().trim();
                ConfigLib.maxRegisterBrightness = Float.valueOf(registermaxlight);
                String registerminlight = mRegisterLightMin.getText().toString().trim();
                ConfigLib.minRegisterBrightness = Float.valueOf(registerminlight);
                String registerblur = mRegisterBlur.getText().toString().trim();
                ConfigLib.blurRegisterThreshold = Float.valueOf(registerblur);
                String faceminima = mFaceMinima.getText().toString().trim();
                ConfigLib.registerPicRect = Integer.parseInt(faceminima);
                setSucc(ConfigLib.registerPicRect);
                setSucc(ConfigLib.maxRegisterBrightness +" | "+ ConfigLib.minRegisterBrightness +" | "+ ConfigLib.blurRegisterThreshold +" | "+ ConfigLib.registerPicRect);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MAXREGISTERBRIGHTNESS,ConfigLib.maxRegisterBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_MINREGISTERBRIGHTNESS,ConfigLib.minRegisterBrightness).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_BLURREGISTERTHRESHOLD,ConfigLib.blurRegisterThreshold).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_FACEMINIMA,ConfigLib.registerPicRect).commit();
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

            case R.id.setting_detect:
                String rate = mDetectRate.getText().toString().trim();
                float floatRate = Float.parseFloat(rate);
                String size = mTrackerSize.getText().toString().trim();
                float floatSize = Float.parseFloat(size);
                String iSize = mDetectSize.getText().toString().trim();
                int intSize = Integer.parseInt(iSize);
                String iFSize = mFeatureSize.getText().toString().trim();
                int intFSize = Integer.parseInt(iFSize);
                String rFSize = mTrackerFaceSize.getText().toString().trim();
                float fRect = Float.parseFloat(rFSize);
                ConfigLib.picScaleRate = floatRate;
                ConfigLib.picScaleSize = floatSize;
                ConfigLib.Nv21ToBitmapScale = intFSize;
                FaceDetectManager.setFaceMinRect(intSize);
                ConfigLib.minRecognizeRect = fRect;
                setSucc(ConfigLib.picScaleRate +" | "+ConfigLib.picScaleSize+" | "+FaceDetectManager.getFaceMinRect()+" | "+ConfigLib.minRecognizeRect);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_DETECT_RATE,ConfigLib.picScaleRate).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_DETECT_SIZE,FaceDetectManager.getFaceMinRect()).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_TRACKER_SIZE,ConfigLib.picScaleSize).commit();
                AttApp.sp.edit().putInt(AttConstants.PREFS_FEATURE_SIZE,ConfigLib.Nv21ToBitmapScale).commit();
                AttApp.sp.edit().putFloat(AttConstants.PREFS_FACE_SIZE,ConfigLib.minRecognizeRect).commit();
                break;

            case R.id.setting_infrared:
                String irate = mInfraredValue.getText().toString().trim();
                float floatiRate = Float.parseFloat(irate);
                ConfigLib.livenessInFraredThreshold = floatiRate;
                setSucc(ConfigLib.livenessInFraredThreshold);
                AttApp.sp.edit().putFloat(AttConstants.PREFS_INFRAREDVALUE,ConfigLib.livenessInFraredThreshold).commit();
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){

            case R.id.infrared_switch:
                ConfigLib.detectWithInfraredLiveness = isChecked;
                mInfrared_switch.setChecked(ConfigLib.detectWithInfraredLiveness);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_DETECTINFRARED,ConfigLib.detectWithInfraredLiveness).commit();
                ToastUtils.showShort(getResources().getString(R.string.reopen));
                break;

            case R.id.saveinfrared_switch:
                mSaveInfrared_switch.setChecked(isChecked);
                FaceDetectManager.setInfraredDebugPicsState(isChecked);
                break;

            case R.id.saveinfraredlive_switch:
                Constants.DEBUG_SAVE_INFRARED_LIVE = isChecked;
                mSaveInfraredLive_switch.setChecked(Constants.DEBUG_SAVE_INFRARED_LIVE);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_SAVEINFRAREDLIVE,Constants.DEBUG_SAVE_INFRARED_LIVE).commit();
                break;

            case R.id.saveinfraredfake_switch:
                Constants.DEBUG_SAVE_INFRARED_FAKE = isChecked;
                mSaveInfraredFake_switch.setChecked(Constants.DEBUG_SAVE_INFRARED_FAKE);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_SAVEINFRAREDFAKE,Constants.DEBUG_SAVE_INFRARED_FAKE).commit();
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

            case R.id.savefake_switch:
                Constants.DEBUG_SAVE_FAKE = isChecked;
                mSaveF_switch.setChecked(Constants.DEBUG_SAVE_FAKE);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_FAKE,Constants.DEBUG_SAVE_FAKE).commit();
                break;

            case R.id.savelive_switch:
                Constants.DEBUG_SAVE_LIVE = isChecked;
                mSaveL_switch.setChecked(Constants.DEBUG_SAVE_LIVE);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_LIVE,Constants.DEBUG_SAVE_LIVE).commit();
                break;

            case R.id.savet_switch:
                Constants.DEBUG_SAVE_TRACKER = isChecked;
                mSaveTracker_switch.setChecked(Constants.DEBUG_SAVE_TRACKER);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_TRACKER,Constants.DEBUG_SAVE_TRACKER).commit();
                break;

            case R.id.savetan_switch:
                Constants.DEBUG_SAVE_LIVEPIC_SDK = isChecked;
                mSaveTan_switch.setChecked(Constants.DEBUG_SAVE_LIVEPIC_SDK);
                AttApp.sp.edit().putBoolean(AttConstants.PREFS_ST,Constants.DEBUG_SAVE_LIVEPIC_SDK).commit();
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
            mRSf.setVisibility(View.VISIBLE);
            mRSfv.setVisibility(View.VISIBLE);
            mRSl.setVisibility(View.VISIBLE);
            mRSlv.setVisibility(View.VISIBLE);
            mRSt.setVisibility(View.VISIBLE);
            mRStv.setVisibility(View.VISIBLE);
            mRStan.setVisibility(View.VISIBLE);
            mRStanv.setVisibility(View.VISIBLE);
        }else {
            mRSb.setVisibility(View.GONE);
            mRSbv.setVisibility(View.GONE);
            mRSn.setVisibility(View.GONE);
            mRSnv.setVisibility(View.GONE);
            mRSs.setVisibility(View.GONE);
            mRSsv.setVisibility(View.GONE);
            mRSf.setVisibility(View.GONE);
            mRSfv.setVisibility(View.GONE);
            mRSl.setVisibility(View.GONE);
            mRSlv.setVisibility(View.GONE);
            mRSt.setVisibility(View.GONE);
            mRStv.setVisibility(View.GONE);
            mRStan.setVisibility(View.GONE);
            mRStanv.setVisibility(View.GONE);
        }
    }

}
