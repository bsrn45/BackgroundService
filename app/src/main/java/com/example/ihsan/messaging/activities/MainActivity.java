package com.example.ihsan.messaging.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ihsan.messaging.R;
import com.example.ihsan.messaging.db.MyService;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mainintent = new Intent(this, MyService.class);
        mainintent.setAction("main");
        startService(mainintent);


        //Toast.makeText(this,"Main",Toast.LENGTH_SHORT).show();



    }//End of onCreate()


    public void stopService(View view){

        //Toast.makeText(this,"button",Toast.LENGTH_SHORT).show();
        stopService(new Intent(this, MyService.class));

    }//End of stopButton onClick



}
