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
import android.widget.Button;
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

public class Login extends AppCompatActivity implements View.OnClickListener{


    private boolean register = false;

    private String accountEmail;

    EditText name;
    EditText email;
    EditText password;

    Button btn_first_toregister, btn_first_tologin, btn_signup_register, btn_login_submit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences config = getSharedPreferences("config", Context.MODE_PRIVATE);
        accountEmail = config.getString("accountEmail",null);

                if(accountEmail == null){
                    callFirstPage();

                    Toast.makeText(getApplicationContext(), "Config bulunamadı", Toast.LENGTH_SHORT).show();

                }else {
                    isRegistered(); //sets register globally

                    Toast.makeText(getApplicationContext(), "Config bulundu: "+accountEmail, Toast.LENGTH_SHORT).show();

                    if(register) {
                            callMain();

                        }else {

                            callFirstPage();
                        }
                }//End of if login


    }//End of onCreate


    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.btn_toregister:
                setContentView(R.layout.activity_signup);

                name = (EditText) findViewById(R.id.et_name_s);
                email = (EditText) findViewById(R.id.et_email_s);
                password = (EditText) findViewById(R.id.et_password_s);

                btn_signup_register = (Button) findViewById(R.id.btn_Register);
                btn_signup_register.setOnClickListener((android.view.View.OnClickListener) this);

                setTitle("Register Messaging App");
                break;

            case R.id.btn_tologin:
                setContentView(R.layout.activity_login);

                email = (EditText) findViewById(R.id.et_email_l);
                password = (EditText) findViewById(R.id.et_password_l);

                btn_login_submit = (Button) findViewById(R.id.btn_Submit);
                btn_login_submit.setOnClickListener((android.view.View.OnClickListener) this);

                setTitle("Login Messaging App");
                break;

            case R.id.btn_Register:

                register();
                break;

            case R.id.btn_Submit:

                login();

                break;

        }//End of switch
    }//end of onclick


    private void callFirstPage() {
        setContentView(R.layout.activity_first);

        btn_first_toregister = (Button) findViewById(R.id.btn_toregister);
        btn_first_toregister.setOnClickListener((android.view.View.OnClickListener) this);

        btn_first_tologin = (Button) findViewById(R.id.btn_tologin);
        btn_first_tologin.setOnClickListener((android.view.View.OnClickListener) this);

        setTitle("Welcome Messaging App");
    }//end of callfirstpage

    private void callMain() {
        Intent toMain = new Intent(this, MainActivity.class);
        toMain.putExtra("Acoount", accountEmail);
        startActivity(toMain);

        this.finish();
    }//end of callmain


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

                                if (obj.getInt("registered") == 1) {
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
    public void register(){



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
                                    Toast.makeText(getApplicationContext(), "User registered with: "+ email.getText().toString(), Toast.LENGTH_SHORT).show();
                                    register = true;
                                    return;

                                }else {
                                    register = false;
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

        if(register)
            callMain();
    }//End of register




    private void login() {

        Thread t = new Thread(){

            public void run(){
                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),20000);
                HttpResponse response;

                try{

                    HttpPost post = new HttpPost("http://ihsanbasaran.info.preview.services/loginUser.php");

                    List<NameValuePair> dlist_submit = new ArrayList<NameValuePair>(2);

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

                                if (obj.getInt("login") == 1) {

                                    register = true;
                                    Toast.makeText(getApplicationContext(), "User found: "+ obj.getString("name"), Toast.LENGTH_SHORT).show();
                                    saveUserConfig();
                                    return;

                                }else {

                                    register = false;
                                    Toast.makeText(getApplicationContext(), "User not found!!!", Toast.LENGTH_SHORT).show();
                                    password.setText("");
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

        if(register)
            callMain();

    }//end of login

    private void saveUserConfig() {

        SharedPreferences config = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = config.edit();

        editor.clear();

        editor.putString("accountEmail", email.getText().toString());
        editor.commit();

        Toast.makeText(getApplicationContext(), "Confige kaydedildi", Toast.LENGTH_SHORT).show();


    }//end of saveUserConfig
}
