package com.aiwinn.faceattendance.common;

import android.os.Environment;

import com.aiwinn.facedetectsdk.common.Status;

import java.io.File;

/**
 * com.aiwinn.faceattendance.common
 * SDK_ATT
 * 2018/08/24
 * Created by LeoLiu on User
 */

public class AttConstants {

    public static final String PREFS = "ATT_SP";
    public static boolean DEBUG = true;
    public static boolean INIT_STATE = false;
    public static Status INIT_STATE_ERROR = null;

    public static boolean hasBackCamera = true;
    public static boolean hasFrontCamera = true;

    public static int cameraCount = 2;

    public static boolean LEFT_RIGHT = false;
    public static boolean TOP_BOTTOM = false;

    public static int CAMERA_ID = 0;
    public static int CAMERA_DEGREE = 0;
    public static int PREVIEW_DEGREE = 0;

    public static int CAMERA_PREVIEW_WIDTH = 0;
    public static int CAMERA_PREVIEW_HEIGHT = 0;

    public static int CAMERA_BACK_ID = 0;
    public static int CAMERA_BACK_DEGREE = 0;

    public static int CAMERA_FRONT_ID = 1;
    public static int CAMERA_FRONT_DEGREE = 90;

    public static int DETECT_LIST_SIZE = 10;

    public static String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String PATH_AIWINN = SD_CARD+ File.separator+"aiwinn";

    public static String PATH_ATTENDANCE = PATH_AIWINN+ File.separator+"attendance";

    public static String PATH_BULK_REGISTRATION = PATH_ATTENDANCE+ File.separator+"bulkregistration";
    public static String PATH_CARD = PATH_ATTENDANCE+ File.separator+"testslotcard";

    public static final String PREFS_DETECT_RATE = "DETECT_RATE";
    public static final String PREFS_DETECT_SIZE = "DETECT_SIZE";
    public static final String PREFS_TRACKER_SIZE = "TRACKER_SIZE";
    public static final String PREFS_FEATURE_SIZE = "FEATURE_SIZE";
    public static final String PREFS_CAMERA_ID = "CAMERA_ID";
    public static final String PREFS_CAMERA_DEGREE = "CAMERA_DEGREE";
    public static final String PREFS_PREVIEW_DEGREE = "PREVIEW_DEGREE";
    public static final String PREFS_CAMERA_PREVIEW_SIZE = "CAMERA_PREVIEW_SIZE";
    public static final String PREFS_RECOGNITION = "RECOGNITION";
    public static final String PREFS_TRACKER_MODE = "TRACKERMODE";
    public static final String PREFS_DETECT_MODE = "DETECTMODE";
    public static final String PREFS_LIVENESS = "LIVENESS";
    public static final String PREFS_UNLOCK = "UNLOCK";
    public static final String PREFS_LIVENESST = "LIVENESST";
    public static final String PREFS_FACEMINIMA = "FACEMINIMA";
    public static final String PREFS_SAVEBLURDATA = "SAVEBLURDATA";
    public static final String PREFS_SAVENOFACEDATA = "SAVENOFACEDATA";
    public static final String PREFS_SAVESSDATA = "SAVESSDATA";
    public static final String PREFS_DEBUG = "DEBUG";
    public static final String PREFS_LR = "LR";
    public static final String PREFS_TB = "TB";
    public static final String PREFS_MAXREGISTERBRIGHTNESS = "MAXREGISTERBRIGHTNESS";
    public static final String PREFS_MINREGISTERBRIGHTNESS = "MINREGISTERBRIGHTNESS";
    public static final String PREFS_BLURREGISTERTHRESHOLD = "BLURREGISTERTHRESHOLD";
    public static final String PREFS_MAXRECOGNIZEBRIGHTNESS = "MAXDETECTBRIGHTNESS";
    public static final String PREFS_MINRECOGNIZEBRIGHTNESS = "MINDETECTBRIGHTNESS";
    public static final String PREFS_BLURECOGNIZETHRESHOLD = "BLURDETECTTHRESHOLD";
    public static final String PREFS_BLURECOGNIZENEWTHRESHOLD = "BLURDETECTNEWTHRESHOLD";
}
