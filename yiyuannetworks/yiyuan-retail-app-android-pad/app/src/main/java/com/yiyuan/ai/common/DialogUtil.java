package com.yiyuan.ai.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.yiyuan.ai.AiMainActivity;
import com.yiyuan.ai.R;
import com.yiyuan.ai.activity.GameMainActivity;
import com.yiyuan.ai.activity.PerfectMessageActivity;
import com.yiyuan.ai.activity.RegisterActivity;
import com.yiyuan.ai.model.CustomerModel;

import java.lang.reflect.Field;

/**
 * Created by wangyu on 2019/4/12.
 */

public class DialogUtil {
    public interface FeedBack{
        void sure();
        void cancel();
    }

    public static AlertDialog registerDialog;
    /**
     * 扫描到未注册用户，提示是否注册
     * @param context
     */
    public static void registerDialog(final Context context,final FeedBack feedBack){
        if(registerDialog != null && registerDialog.isShowing())return;
        AlertDialog.Builder builderRegister = new AlertDialog.Builder(context);
        builderRegister.setIcon(R.drawable.ic_logo);
        builderRegister.setTitle(context.getString(R.string.dialog_register));
        builderRegister.setPositiveButton(context.getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                feedBack.sure();
            }
        });
        builderRegister.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                feedBack.cancel();
            }
        });
        registerDialog = builderRegister.show();
    }

    /**
     * 退出dialog
     * @param context
     */
    public static void finishActivity(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(context.getString(R.string.dialog_back_main));
        builder.setPositiveButton(context.getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                ((Activity)context).finish();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builder.show();
    }


    public static void finishGameActivity(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(context.getString(R.string.dialog_back_game));
        builder.setPositiveButton(context.getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                ((Activity)context).finish();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builder.show();
    }


    /**
     * 是否完善信息dialog
     * @param context
     * @param type 1为新增用户进行完善进行完善，2为信息查询进行完善
     */
    public static void finishMessage(final Context context, final CustomerModel customerModel, final int type){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_logo);
        builder.setTitle(context.getString(R.string.dialog_perfect_message));
        builder.setPositiveButton(context.getResources().getString(R.string.sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                Activity activity = ((Activity)context);
                Intent intent = new Intent(activity, PerfectMessageActivity.class);
                intent.putExtra("customerModel",customerModel);
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                ((Activity)context).finish();
            }
        });
        builder.show();
    }

    /**
     * 输入提示框
     */
    public static void editDialog(Context context,String title,final EditDialogText editDialogText){
        final EditText et = new EditText(context);
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(title)
                .setIcon(R.drawable.ic_logo)
                .setView(et)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.dialog_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editDialogText.getText(et.getText().toString());
                    }
                }).setNegativeButton(context.getString(R.string.dialog_cancel),null).show();
    }

    private static AlertDialog dialog;
    public static void showDialog(Context context,String msg) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(false);
        if(!dialog.isShowing()) {
            dialog.show();
        }
    }

    public static void dissmisDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    public interface EditDialogText{
        void getText(String text);
    }
}
