package com.amap.locationservicedemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 通过后台服务持续定位
 */
public class MainActivity extends AppCompatActivity {

    private Button buttonStartService;
    private TextView tvResult;

    public static final String RECEIVER_ACTION = "location_in_background";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartService = (Button) findViewById(R.id.button_start_service);
        tvResult = (TextView) findViewById(R.id.tv_result);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION);
        registerReceiver(locationChangeBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (locationChangeBroadcastReceiver != null)
            unregisterReceiver(locationChangeBroadcastReceiver);

        super.onDestroy();
    }

    /**
     * 启动定位服务
     * @param view
     */
    public void startService(View view) {
        if (buttonStartService.getText().toString().equals(getResources().getString(R.string.startLocation))) {

            getApplicationContext().startService(new Intent(this, LocationService.class));
            buttonStartService.setText(R.string.stopLocation);
            tvResult.setText("正在定位...");

            startAlarmManager();

        } else {
            getApplicationContext().stopService(new Intent(this, LocationService.class));
            buttonStartService.setText(R.string.startLocation);
            tvResult.setText("");

        }
    }

    private void startAlarmManager() {
        Intent intent = new Intent("LOCATION_CLOCK");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        // 没五秒唤醒一次
        long second = 15 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), second, pendingIntent);
    }


     private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(RECEIVER_ACTION)){
                String locationResult = intent.getStringExtra("result");
                if (null != locationResult && !locationResult.trim().equals("")) {
                    tvResult.setText(locationResult);
                }
            }
        }
    };

}
