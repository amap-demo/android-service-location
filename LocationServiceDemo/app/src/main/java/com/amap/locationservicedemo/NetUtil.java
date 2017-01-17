package com.amap.locationservicedemo;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

/**
 * Created by liangchao_suxun on 17/1/16.
 * 用于判断设备是否可以访问网络。
 */

public class NetUtil {

    private static class Holder {
        public static NetUtil instance = new NetUtil();
    }

    public static NetUtil getInstance() {
        return Holder.instance;
    }

    /**
     * 设备是否可以访问网络
     *
     * @return
     */
    public boolean isNetAvaliable(Context context) {
        boolean hasWifoCon = false;
        boolean hasMobileCon = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {

            String type = net.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                if (net.isConnected()) {
                    hasWifoCon = true;
                }
            }

            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
                }
            }
        }

        return hasWifoCon || hasMobileCon;
    }
}
