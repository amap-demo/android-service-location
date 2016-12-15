package com.amap.locationservicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * 包名： com.amap.locationservicedemo
 * <p>
 * 创建时间：2016/11/9
 * 项目名称：LocationServiceDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：
 */
public class AlarmReceiver extends BroadcastReceiver {
    private String TAG = "AlarmReceiver";
    private PowerManager powerManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("LOCATION_CLOCK")) {
            Log.e(TAG, "--->>>   onReceive  LOCATION_CLOCK");
            Intent locationIntent = new Intent(context, LocationService.class);
            context.startService(locationIntent);
            if (powerManager == null) {
                //针对熄屏后cpu休眠导致的无法联网、定位失败问题,通过定期点亮屏幕实现联网,本操作会导致cpu无法休眠耗电量增加,谨慎使用
                powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
                wl.acquire();
                //点亮屏幕
                wl.release();
                //释放
            }
        }
    }
}
