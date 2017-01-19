package com.amap.locationservicedemo;

import android.content.Context;

/**
 * Created by liangchao_suxun on 17/1/19.
 * 代理类，用于处理息屏造成wifi被关掉时再重新点亮屏幕的逻辑
 */

public interface IWifiAutoCloseDelegate {


    /**
     * 判断在该机型下此逻辑是否有效。目前已知的系统是小米系统存在(用户自助设置的)息屏断掉wifi的功能。
     *
     * @param context
     * @return
     */
    public boolean isUseful(Context context);


    /**
     * 点亮屏幕的服务有可能被重启。此处进行初始化
     *
     * @param context
     * @return
     */
    public void initOnServiceStarted(Context context);


    /**
     * 定位成功时，如果移动网络无法访问，而且屏幕是点亮状态，则对状态进行保存
     */
    public void onLocateSuccess(Context context, boolean isScreenOn, boolean isMobileable);

    /**
     * 对定位失败情况的处理
     */
    public void onLocateFail(Context context, int errorCode, boolean isScreenOn, boolean isWifiable);


}
