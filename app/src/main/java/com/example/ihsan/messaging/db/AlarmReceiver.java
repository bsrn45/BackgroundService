package com.example.ihsan.messaging.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmintent = new Intent(context, MyService.class);
        alarmintent.setAction("alarm");
        context.startService(alarmintent);

        Toast.makeText(context,"alarm geldi",Toast.LENGTH_SHORT).show();






    }//End of onReceive





}
