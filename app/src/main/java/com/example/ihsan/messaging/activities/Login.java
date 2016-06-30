package com.example.ihsan.messaging.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihsan.messaging.R;
import com.example.ihsan.messaging.db.MyService;

import org.apache.http.HttpEntity;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {


    private boolean register = false;

    private String accountEmail;

    EditText name;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences config = getSharedPreferences("config", Context.MODE_PRIVATE);
        accountEmail = config.getString("accountEmail",null);

                if(accountEmail == null){
                    setContentView(R.layout.activity_login);
                    Toast.makeText(getApplicationContext(), "Config bulunamadı", Toast.LENGTH_SHORT).show();

                }else {
                    isRegistered(); //sets register globally
                    Toast.makeText(getApplicationContext(), "Config bulundu: "+accountEmail, Toast.LENGTH_SHORT).show();

                    if(register) {


                            Intent toMain = new Intent(this, MainActivity.class);
                            toMain.putExtra("Acoount", accountEmail);
                            startActivity(toMain);

                            this.finish();

                        }else {

                            setContentView(R.layout.activity_login);
                        }
                }//End of if login



        name = (EditText) findViewById(R.id.et_name);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);




    }//End of onCreate


//*************************************************
    //check user
    private void isRegistered() {

        Thread t = new Thread(){

            public void run(){
                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),20000);
                HttpResponse response;

                try{

                    HttpPost post = new HttpPost("http://ihsanbasaran.info.preview.services/checkUser.php");

                    List<NameValuePair> dlist_submit = new ArrayList<NameValuePair>(2);

                    dlist_submit.add(new BasicNameValuePair("accountEmail",accountEmail));


                    post.setEntity(new UrlEncodedFormEntity(dlist_submit,"UTF-8"));

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

                                if (obj.getInt("var") == 1) {
                                    Toast.makeText(getApplicationContext(), "User found: "+ obj.getString("name"), Toast.LENGTH_SHORT).show();
                                    register = true;
                                    return;

                                }else {

                                    register = false;
                                    return;
                                }
                            }
                        }


                    }else //if response
                        Toast.makeText(getApplicationContext(), "Cevap gelmedi", Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Log.e("Hata: ", String.valueOf(e));
                }

                Looper.loop();
            }
        };

        t.start();





    }//End of isRegistered


//**************************************************************************************
    public void btnSubmit(View view){

//Geçici confige yaz**********************
        SharedPreferences config = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = config.edit();

        editor.putString("accountEmail", email.getText().toString());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Confige kaydedildi", Toast.LENGTH_SHORT).show();
 //******************************************

        Thread t = new Thread(){

            public void run(){
                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),20000);
                HttpResponse response;

                try{

                    HttpPost post = new HttpPost("http://ihsanbasaran.info.preview.services/registerUser.php");

                    List<NameValuePair> dlist_submit = new ArrayList<NameValuePair>(2);

                    dlist_submit.add(new BasicNameValuePair("name",name.getText().toString()));
                    dlist_submit.add(new BasicNameValuePair("email",email.getText().toString()));
                    dlist_submit.add(new BasicNameValuePair("password",password.getText().toString()));



                    post.setEntity(new UrlEncodedFormEntity(dlist_submit,"UTF-8"));

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

                                if (obj.getInt("success") == 1) {
                                    Toast.makeText(getApplicationContext(), "User registered with: "+ obj.getString("accountEmail"), Toast.LENGTH_SHORT).show();

                                    Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                                    toMain.putExtra("Acoount",obj.getString("accountEmail"));
                                    startActivity(toMain);

                                    return;

                                }else {

                                    Toast.makeText(getApplicationContext(), "User can't registered with: "+ email.getText().toString(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }


                    }else //if response
                        Toast.makeText(getApplicationContext(), "Cevap gelmedi", Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Log.e("Hata: ", String.valueOf(e));
                }

                Looper.loop();
            }
        };

        t.start();
    }//End of btnSubmit
}
