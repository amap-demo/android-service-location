package com.amap.locationservicedemo;

import android.content.Context;
import android.os.PowerManager;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liangchao_suxun on 17/1/16.
 * 获得PARTIAL_WAKE_LOCK	， 保证在息屏状体下，CPU可以正常运行
 */

public class PowerManagerUtil {
    private static class Holder {
        public static PowerManagerUtil instance = new PowerManagerUtil();
    }


    private PowerManager pm = null;

    private PowerManager.WakeLock pmLock = null;

    /**
     * 上次唤醒屏幕的触发时间
     */
    private long mLastWakupTime = System.currentTimeMillis();

    /**
     * 最小的唤醒时间间隔，防止频繁唤醒。默认为15秒钟
     */
    private long mMinWakupInterval = 15 * 1000;

    /**
     * 内部线程工厂
     */
    private InnerThreadFactory mInnerThreadFactory = null;

    public static PowerManagerUtil getInstance() {
        return Holder.instance;
    }

    /**
     * 判断屏幕是否处于点亮状态
     *
     * @param context
     */
    public boolean isScreenOn(final Context context) {
        try {
            Method isScreenMethod = PowerManager.class.getMethod("isScreenOn",
                    new Class[]{});
            if (pm == null) {
                pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            }
            boolean screenState = (Boolean) isScreenMethod.invoke(pm);
            return screenState;
        } catch (Exception e) {
            return true;
        }
    }


    /**
     * 唤醒屏幕
     */
    public void wakeUpScreen(final Context context) {

        try {
            acquirePowerLock(context, PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据levelAndFlags，获得PowerManager的WaveLock
     * 利用worker thread去获得锁，以免阻塞主线程
     * @param context
     * @param levelAndFlags
     */
    private void acquirePowerLock(final Context context, final int levelAndFlags) {
        if (context == null) {
            throw new NullPointerException("when invoke aquirePowerLock ,  context is null which is unacceptable");
        }

        long currentMills = System.currentTimeMillis();

        if (currentMills - mLastWakupTime < mMinWakupInterval) {
            return;
        }


        mLastWakupTime = currentMills;

        if (mInnerThreadFactory == null) {
            mInnerThreadFactory = new InnerThreadFactory();
        }

        mInnerThreadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                if (pm == null) {
                    pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                }

                if (pmLock != null) { // release
                    pmLock.release();
                    pmLock = null;
                }

                pmLock = pm.newWakeLock(levelAndFlags, "MyTag");
                pmLock.acquire();
                pmLock.release();
            }
        }).start();
    }


    /**
     * 线程工厂
     */
    private class InnerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable);
        }
    }
}
