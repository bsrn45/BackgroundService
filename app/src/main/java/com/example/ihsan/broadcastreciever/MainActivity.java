package com.example.ihsan.broadcastreciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public AlarmManager alarmMgr;
    public PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAlarm();

        Toast.makeText(this,"Main",Toast.LENGTH_SHORT).show();
    }


    public void stopService(View view){

        //Toast.makeText(this,"button",Toast.LENGTH_SHORT).show();
        stopService(new Intent(this, MyService.class));
        closeAlarm();
    }

    private void setAlarm(){


        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , alarmIntent);
    }

    public void closeAlarm(){

        alarmMgr.cancel(alarmIntent);
    }

}
