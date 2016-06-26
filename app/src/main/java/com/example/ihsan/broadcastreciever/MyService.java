package com.example.ihsan.broadcastreciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    public AlarmManager alarmMgr;
    public PendingIntent alarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(this,"Service Start",Toast.LENGTH_SHORT).show();

        checkDB();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service Stopped",Toast.LENGTH_SHORT).show();
        closeAlarm();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service and Alarm Created",Toast.LENGTH_SHORT).show();
        setAlarm();

    }

    private void setAlarm(){


        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10 , alarmIntent);
    }

    public void closeAlarm(){

        alarmMgr.cancel(alarmIntent);
    }

    //******************************************************************************************

    public void checkDB(){

        Thread t = new Thread(){

            public void run(){

                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),20000);
                HttpResponse response;

                try{

                    HttpPost post = new HttpPost("http://ihsanbasaran.info.preview.services/check.php");

                    String html = "";
                    response = client.execute(post);  //Gönderildi, cevap bekleniyor

                    if(response != null){  //Cevap varsa ....

                        InputStream in = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        StringBuilder builder = new StringBuilder();


                        for (String line = null; (line = reader.readLine()) != null;) {
                            builder.append(line).append("\n");
                            //Toast.makeText(getApplicationContext(),line, Toast.LENGTH_SHORT).show();
                        }
                        JSONTokener tokener = new JSONTokener(builder.toString());
                        JSONArray finalResult = new JSONArray(tokener);

                        if(finalResult.length() > 0){

                            for(int i=0; i<finalResult.length(); i++) {

                                JSONObject obj = finalResult.getJSONObject(i);

                                if (obj.getInt("okundu") == 0) {
                                    Toast.makeText(getApplicationContext(), "Yeni mesaj: "+ obj.getString("name"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }


                        /*String rows = null;

                        try {

                            while ((rows = reader.readLine()) != null){

                                sb.append(rows + "\n");
                                //Toast.makeText(getApplicationContext(),reader.readLine(), Toast.LENGTH_SHORT).show();
                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            try {
                                in.close();
                            }catch (IOException e){
                                e.printStackTrace();

                            }
                        }

                        html = sb.toString();

                        JSONArray split = new JSONArray(html);
                        if(split.length() > 0){

                            for(int i=0; i<split.length(); i++) {

                                JSONObject obj = split.getJSONObject(i);

                                if (obj.getInt("okundu") == 0) {
                                    Toast.makeText(getApplicationContext(), "Yeni mesaj: "+ obj.getString("name"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }*/

                    }else //if response
                        Toast.makeText(getApplicationContext(), "Cevap gelmedi", Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Log.e("Hata: ", String.valueOf(e));
                }

                Looper.loop();
            }
        };

        t.start();
    }
}
