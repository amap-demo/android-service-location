package com.amap.locationservicedemo;

import android.content.Context;

import com.amap.api.location.AMapLocation;

/**
 * Created by liangchao_suxun on 17/1/16.
 * 在定位失败的情况下，用于判断当前定位错误是否是由于息屏导致的网络关闭引起的。
 */

public class LocationStatusManager {

    /**
     * 上一次的定位是否成功
     */
    private boolean mPriorSuccLocated = false;

    /**
     * 屏幕亮时可以定位
     */
    private boolean mPirorLocatableOnScreen = false;


    static class Holder {
        public static LocationStatusManager instance = new LocationStatusManager();
    }

    public static LocationStatusManager getInstance() {
        return Holder.instance;
    }

    /**
     * 定位成功时，重置为定位成功的状态
     *
     * @param isScreenOn 当前屏幕是否为点亮状态
     */
    public void onLocationSuccess(boolean isScreenOn) {
        mPriorSuccLocated = true;
        if (isScreenOn) {
            mPirorLocatableOnScreen = true;
        }
    }

    /**
     * 判断是否由屏幕关闭导致的定位失败。
     * 只有在 网络可访问&&errorCode==4&&（priorLocated&&locatableOnScreen) && !isScreenOn 才认为是有息屏引起的定位失败
     * 如果判断条件较为严格，请按需要适当修改
     *
     * @param errorCode  定位错误码, 0=成功， 4=因为网络原因造成的失败
     * @param isScreenOn 当前屏幕是否为点亮状态
     */
    public boolean isFailOnScreenOff(Context context, int errorCode, boolean isScreenOn) {
        return NetUtil.getInstance().isNetAvaliable(context) && errorCode == AMapLocation.ERROR_CODE_FAILURE_CONNECTION && (mPriorSuccLocated && mPirorLocatableOnScreen) && !isScreenOn;
    }
}
