package cn.jcyh.utils;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

import cn.jcyh.doorbelldemo.function.ui.DoorbellHomeActivity;
import cn.jcyh.eaglekinglockdemo.R;

/**
 * Created by jogger on 2018/8/3.
 */
public class NotificationUtil {
    private Map<String, String> mChannelMap;
    private static NotificationUtil sNotificationUtil;

    private NotificationUtil() {
        configChannel();
    }

    private void configChannel() {
        mChannelMap = new HashMap<>();
        mChannelMap.put("1", "doorbellPush");
    }


    public static NotificationUtil getNotificationUtil() {
        if (sNotificationUtil == null) {
            synchronized (NotificationUtil.class) {
                if (sNotificationUtil == null)
                    sNotificationUtil = new NotificationUtil();
            }
        }
        return sNotificationUtil;
    }

    public void createNotification(String channelID, String channelName, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(Utils.getApp(), channelID);
        notification.setAutoCancel(true)
                .setContentText(message)
                .setContentTitle(Utils.getApp().getString(R.string.new_msg))
                .setTicker(Utils.getApp().getString(R.string.new_msg))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        Intent mIntent = new Intent(Utils.getApp(), DoorbellHomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(Utils.getApp(), 0, mIntent, 0);
        notification.setContentIntent(pendingIntent);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        assert notificationManager != null;
        notificationManager.notify(2, notification.build());

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) Utils.getApp().getSystemService(
                Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
}
