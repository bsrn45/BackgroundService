package com.example.ihsan.broadcastreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, MyService.class));



        //Toast.makeText(context,"alarm geldi",Toast.LENGTH_SHORT).show();


    }


}
