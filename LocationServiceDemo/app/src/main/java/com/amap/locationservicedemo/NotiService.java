package com.amap.locationservicedemo;

import android.app.Service;
import android.content.Intent;
import android.app.Notification;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by liangchao_suxun on 17/1/16.
 * 提供了2种提高进程优先级的机制，建议第1种
 * 1. 利用双service进行notification绑定，进而将Service的OOM_ADJ提高到1
 * 2. 关闭屏幕时启动一个Activity来提高优先级
 */

public class NotiService extends Service {

    /**
     * startForeground的 noti_id
     */
    private static int NOTI_ID = 123321;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 触发利用notification增加进程优先级
     */
    public void applyNotiKeepMech(){
        startForeground(NOTI_ID, buildNotification(getBaseContext()));
        startService(new Intent(this, NotiInnerService.class));
    }

    public void unApplyNotiKeepMech(){
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class NotiInnerService extends Service {
        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(NOTI_ID, buildNotification(getBaseContext()));
            stopForeground(true);
            stopSelf();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


    private static Notification buildNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentText("service");
        return builder.getNotification();
    }
}
