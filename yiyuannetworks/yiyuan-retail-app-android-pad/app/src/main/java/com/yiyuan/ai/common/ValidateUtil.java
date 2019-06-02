package com.yiyuan.ai.common;

import android.text.TextUtils;
import android.widget.EditText;

import com.aiwinn.base.util.StringUtils;
import com.yiyuan.ai.R;
import com.yiyuan.aiwinn.faceattendance.AttApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangyu on 2019/4/15.
 */

public class ValidateUtil {
    /**
     * 验证输入合法性
     * @param editText
     * @param type
     * @param isCanNull 是否可以为空
     */
    public static boolean valiedateInput(EditText editText,String text, int type,boolean isCanNull){
        if(!isCanNull){
            if(TextUtils.isEmpty(text)){
                editText.setError(AttApp.getContext().getString(R.string.validate_not_empty));
                editText.requestFocus();
                return false;
            }
        }
        if(StringUtils.isEmpty(text)){
            return true;
        }
        switch (type){
            //年龄验证
            case 1:
                if(!(isNumeric(text)&&Integer.parseInt(text)>0&&Integer.parseInt(text)<150)){
                    editText.setError(AttApp.getContext().getString(R.string.validate_age_error));
                    editText.requestFocus();
                    return false;
                }
                return true;
            //电话验证
            case 2:
                if(text.length()!=11||!isNumeric(text)){
                    editText.setError(AttApp.getContext().getString(R.string.validate_phone_error));
                    editText.requestFocus();
                    return false;
                }
                return true;
            //验证码验证
            case 3:
                if(text.length()!=6||!isNumeric(text)){
                    editText.setError(AttApp.getContext().getString(R.string.validate_code_error));
                    editText.requestFocus();
                    return false;
                }
                return true;
                //性别验证
            case 4:
                if(!isNumeric(text)||text.length()!=1){
                    editText.setError(AttApp.getContext().getString(R.string.validate_gender_error));
                    editText.requestFocus();
                    return false;
                }
                return true;
            //邮箱验证
            case 5:
                if(!text.contains("@")){
                    editText.setError(AttApp.getContext().getString(R.string.validate_email_error));
                    editText.requestFocus();
                    return false;
                }
                return true;
        }
        return true;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
