package com.amap.locationservicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("LOCATION_CLOCK")) {
            Log.e("ggb", "--->>>   onReceive  LOCATION_CLOCK");
            Intent locationIntent = new Intent(context, LocationService.class);
            context.startService(locationIntent);
        }

    }
}
