package cn.jcyh.doorbelldemo.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import cn.jcyh.doorbelldemo.function.ui.DoorbellHomeActivity;
import cn.jcyh.doorbelldemo.observer.FriendServiceObserver;
import cn.jcyh.doorbelldemo.observer.MessageReceiveObserver;
import cn.jcyh.doorbelldemo.observer.UserStatusObserver;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.manager.ObserverManager;


/**
 * Created by jogger on 2017/12/4.
 * 后台主要服务
 */

public class MainService extends Service {
    private Observer<StatusCode> mUserStatusObserver;
    private Observer<List<IMMessage>> mMessageReceiveObserver;
    private Observer<FriendChangedNotify> mFriendNotifyObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        mUserStatusObserver = new UserStatusObserver();
        mMessageReceiveObserver = new MessageReceiveObserver();
        mFriendNotifyObserver = new FriendServiceObserver();
    }

    private static final String CHANNEL_ID = "service";
    private static final String CHANNEL_NAME = "background";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }
        Intent backIntent = new Intent(this, DoorbellHomeActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, backIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.push_layout)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.runing))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        startForeground(startId, builder.build());
        registerNIMObservers(true);
        return START_STICKY;
    }

    private void registerNIMObservers(boolean register) {
        //用户状态监听
        ObserverManager.observeOnlineStatus(mUserStatusObserver, register);
        //消息接收监听
        ObserverManager.observeReceiveMessage(mMessageReceiveObserver, register);
        //好友更新监听
        ObserverManager.observeFriendChangedNotify(mFriendNotifyObserver, register);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        registerNIMObservers(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
