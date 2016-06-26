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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, MyService.class));

        Toast.makeText(this,"Main",Toast.LENGTH_SHORT).show();
    }


    public void stopService(View view){

        //Toast.makeText(this,"button",Toast.LENGTH_SHORT).show();
        stopService(new Intent(this, MyService.class));

    }



}
