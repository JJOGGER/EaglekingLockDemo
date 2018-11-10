package cn.jcyh.doorbelldemo.function.presenter;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;

import com.netease.nimlib.sdk.AbortableFuture;

import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.DoorbellCallContract;
import cn.jcyh.doorbelldemo.function.model.DoorbellCallModel;
import cn.jcyh.utils.SystemUtil;
import cn.jcyh.utils.Utils;

/**
 * Created by jogger on 2018/7/3.
 */
public class DoorbellCallPresenter extends BasePresenter<DoorbellCallContract.View, DoorbellCallContract.Model> implements DoorbellCallContract.Presenter {
    private MediaPlayer mMediaPlayer;
    private AbortableFuture<Void> mDownloadFuture;

    public DoorbellCallPresenter() {
    }

    @Override
    public void attachView(DoorbellCallContract.View view) {
        super.attachView(view);
        ControlCenter.sIsCalling = true;
        SystemUtil.wakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
        startAlarm();//播放铃声
    }

    @Override
    public void detachView() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
        ControlCenter.sIsCalling = false;
        if (mDownloadFuture != null) {
            mDownloadFuture.abort();
            mDownloadFuture = null;
        }
        super.detachView();
    }

    @Override
    public DoorbellCallContract.Model attacheModel() {
        return new DoorbellCallModel();
    }

    /**
     * 播放系统铃声
     */
    private void startAlarm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //播放系统铃声
                Uri uri = RingtoneManager.getActualDefaultRingtoneUri(Utils.getApp(),
                        RingtoneManager.TYPE_RINGTONE);
                if (uri == null)
                    return;
                mMediaPlayer = MediaPlayer.create(Utils.getApp(), uri);
                if (mMediaPlayer != null) {
                    try {
//                mMediaPlayer.prepare();
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = MediaPlayer.create(Utils.getApp(), uri);
                        }
                        mMediaPlayer.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mMediaPlayer.start();
                            mMediaPlayer.setLooping(true);
                        }
                    });
                }
            }
        }).start();
    }
}
