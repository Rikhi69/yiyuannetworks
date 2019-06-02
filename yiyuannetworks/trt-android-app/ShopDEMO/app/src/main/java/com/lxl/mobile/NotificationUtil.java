package com.lxl.mobile;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.lxl.shop.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/1/29
 */
public class NotificationUtil{

    public static void showNotification(Context context,String title,String content){

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService
//                (NOTIFICATION_SERVICE);
//        /**
//         *  实例化通知栏构造器
//         */
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//
//        /**
//         *  设置Builder
//         */
//        //设置标题
//        mBuilder.setContentTitle(title)
//                //设置内容
//                .setContentText(content)
//                //设置大图标
//                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                //设置小图标
//                .setSmallIcon(R.drawable.main_icon)
//                //设置通知时间
//                .setWhen(System.currentTimeMillis())
//                //首次进入时显示效果
//                .setTicker("我是测试内容")
//                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
//                .setDefaults(Notification.DEFAULT_SOUND);
//        //发送通知请求
//        notificationManager.notify(10, mBuilder.build());


//        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent().setAction(Intent.ACTION_VIEW), 0);
//        Notification notify= new Notification.Builder(context)
//                .setSmallIcon(R.drawable.main_icon)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.main_icon))
//                .setTicker( "您有新短消息，请注意查收！")
//                .setContentTitle(title)
//                .setContentText(content)
//                .setContentIntent(pendingIntent).setNumber(1).build();
//        notify.flags |= Notification.FLAG_AUTO_CANCEL; // FL
//        manager.notify(1,notify);

        String id = "yiyuan_"+context.getPackageName()+"_channel";
        String name="新零售-通知";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
//            Uri mUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//Settings.System.DEFAULT_NOTIFICATION_URI;
//            mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
//            mChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + context.getPackageName() +"/"+ R.raw.n84146), Notification.AUDIO_ATTRIBUTES_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.main_icon).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.main_icon)
                    .setOngoing(true);
                    //.setChannel(id);//无效
            notification = notificationBuilder.build();
        }
        MediaPlayer mMediaPlayer= MediaPlayer.create(context, R.raw.n84146);
        mMediaPlayer.start();
        notificationManager.notify((int)(Math.random()*1000), notification);
    }
}