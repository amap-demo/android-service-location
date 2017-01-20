
# android-service-location
高德定位后台服务的实现

## 前述 ##
- [高德官网申请Key](http://lbs.amap.com/dev/#/).
- 阅读[参考手册](http://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html).
- 工程基于高德地图Android定位SDK实现

## 扫一扫安装##
![Screenshot]( https://raw.githubusercontent.com/amap-demo/android-service-location/master/LocationServiceDemo/apk/1477653836.png)  

## 使用方法##
###1:配置搭建AndroidSDK工程###
- [Android Studio工程搭建方法](http://lbs.amap.com/api/android-sdk/guide/creat-project/android-studio-creat-project/#add-jars).
- [通过maven库引入SDK方法](http://lbsbbs.amap.com/forum.php?mod=viewthread&tid=18786).

###使用场景###
该示例主要展示App切换到后台熄灭屏幕后如何持续获得设备位置。

###问题说明###
小米可以在WLAN高级设置中，设置在息屏情况下关闭wifi，（如果设备手机流量关闭）导致在亮屏时可以定位，息屏后不能定位的情况。针对此种情况，我们在定位服务中，如果检测到上述逻辑，则去点亮屏幕。
小米息屏后，WIFI大概在5分钟之后会断开，所以我们建议点亮屏幕的时间间隔为5分钟。

###核心难点###
1. 在定位服务中检测是否是由息屏造成的网络中断，如果是，则尝试进行点亮屏幕。同时，为了避免频繁点亮，对最小时间间隔进行了设置(可以按需求修改).
如果息屏没有断网，则无需点亮屏幕.
2. 需要保证定位服务的优先级，以免被杀死。
3. 定位服务包含了点亮屏幕的功能，需要有拉活机制，保证此服务一直是alive的。部分机型会杀死尝试多次点亮屏幕的Service(经不完全测试，华为允许的尝试次数为2次，小米为1次).

###实现原理###
1.在本地服务里启动连续定位:
```java
//在activity中启动自定义本地服务LocationService
getApplicationContext().startService(new Intent(this, LocationService.class));

//在LocationService中启动定位
mLocationClient = new AMapLocationClient(this.getApplicationContext());
mLocationOption = new AMapLocationClientOption();
// 使用连续定位
mLocationOption.setOnceLocation(false);
// 每10秒定位一次
mLocationOption.setInterval(10 * 1000);
mLocationClient.setLocationOption(mLocationOption);
mLocationClient.setLocationListener(locationListener);
mLocationClient.startLocation();
```
2.在locationListener中对结果进行判断，如果是息屏造成的断网，则尝试点亮屏幕:
```java
  AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //发送结果的通知
            sendLocationBroadcast(aMapLocation);

	    //判断是否需要对息屏断wifi的情况进行处理
            if (!mIsWifiCloseable) {
                return;
            }

	    //将定位结果和设备状态一起交给mWifiAutoCloseDelegate
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                //...
            } else {
               //...
            }

        }

        private void sendLocationBroadcast(AMapLocation aMapLocation) {
            //记录信息并发送广播...
        }

    };

/** 处理息屏后wifi断开的逻辑*/
public class WifiAutoCloseDelegate implements IWifiAutoCloseDelegate {

    /**
     * 请根据后台数据自行添加。此处只针对小米手机
     * @param context
     * @return
     */
    @Override
    public boolean isUseful(Context context) {
       //...
    }

    /** 由于服务可能被杀掉，所以在服务初始化时，初始相关参数*/
    @Override
    public void initOnServiceStarted(Context context) {
        //...
    }

    /** 处理定位成功的信息*/s
    @Override
    public void onLocateSuccess(Context context, boolean isScreenOn, boolean isMobileable) {
        //...
    }
	
    /** 处理定位失败的信息。如果需要唤醒屏幕，则尝试唤醒*/
    @Override
    public void onLocateFail(Context context, int errorCode, boolean isScreenOn, boolean isWifiable) {
        //...
    }
}
```

3.点亮屏幕时，会利用最小间隔时间加以限制:
```java

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
     * 利用worker thread去获得锁，以免阻塞主线程,并且增加了最小间隔，防止频繁唤醒
     * @param context
     * @param levelAndFlags
     */
    private void acquirePowerLock(final Context context, final int levelAndFlags) {
        //...
    }
```

4.采用双service绑定Notification，提高进程优先级
```java
    /**
     * LocationService.java
     * 触发利用notification增加进程优先级
     */
    protected void applyNotiKeepMech() {
        //...
    }

    /* LocationHelperService
     * binder中的回调用于绑定和LocatioService一样的NotificationId，并stopForeground使通知隐藏
     */
   private class HelperBinder extends ILocationHelperServiceAIDL.Stub{
        @Override
        public void onFinishBind(int notiId) throws RemoteException {
            //...
        }
    }

```

5.采用了LocationHelperService做为守护进程，在检测到LocationService被杀掉后，重启定位服务。
```java
        private void startBind() {
        mInnerConnection = new ServiceConnection() {
	    /** 检测到定位服务被kill掉时，重启定位服务*/
            @Override
            public void onServiceDisconnected(ComponentName name) {
                //...
            }

        };
	//...
    }
```
