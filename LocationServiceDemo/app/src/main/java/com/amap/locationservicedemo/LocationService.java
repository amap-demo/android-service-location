package com.amap.locationservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 包名： com.amap.locationservicedemo
 * <p>
 * 创建时间：2016/10/27
 * 项目名称：LocationServiceDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：后台服务定位
 */
public class LocationService extends Service {

    private String TAG = LocationService.class.getSimpleName();

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private int locationCount;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        stopLocation();
        super.onDestroy();
    }

    /**
     * 启动定位
     */
    void startLocation() {
        stopLocation();

        if(null == mLocationClient){
            mLocationClient = new AMapLocationClient(this.getApplicationContext());
        }

        mLocationOption = new AMapLocationClientOption();
        // 使用连续
        mLocationOption.setOnceLocation(false);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(locationListener);
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    void stopLocation(){
        if(null != mLocationClient){
            mLocationClient.stopLocation();
        }
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            locationCount ++;
            long callBackTime = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            sb.append("定位完成 第" + locationCount +  "次\n");
            sb.append("回调时间: " + Utils.formatUTC(callBackTime, null) + "\n");
            if(null == aMapLocation){
                sb.append("定位失败：location is null!!!!!!!");
            } else {
                sb.append(Utils.getLocationStr(aMapLocation));
            }

            Log.e(TAG, sb.toString());

            Intent mIntent = new Intent(MainActivity.RECEIVER_ACTION);
            mIntent.putExtra("result", sb.toString());

            //发送广播
            sendBroadcast(mIntent);
        }
    };

}
