package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText rmb;
    TextView show;
    Handler handler;
    public final String Tag="RateActivity";
    private double dollarRate;
    private double euroRate;
    private double wonRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb=findViewById(R.id.rmb);
        show=findViewById(R.id.showOut);

        //获取SP中的数据
        SharedPreferences sharedPreferences=getSharedPreferences("myRate", Activity.MODE_PRIVATE);
        //SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate=sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate=sharedPreferences.getFloat("won_rate",0.0f);

        Log.i(Tag,"onCreate:dollarRate="+dollarRate);
        Log.i(Tag,"onCreate:euroRate="+euroRate);
        Log.i(Tag,"onCreate:wonRate="+wonRate);

        //开启子线程
        Thread thread=new Thread(this);
        thread.start();

        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    String str=(String) msg.obj;
                    Log.i(Tag,"onCreate:handleMessage msg="+str);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openCfg();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View btn){
        try {
            String r=rmb.getText().toString();
            double rmb=Double.valueOf(r);
            if (btn.getId()==R.id.btn_dollar){
                show.setText(String.format("%.2f",rmb*dollarRate));
            }else if(btn.getId()==R.id.btn_euro){
                show.setText(String.format("%.2f",rmb*euroRate));
            }else {
                show.setText(String.format("%.2f",rmb*wonRate));
            }
        }
        catch (Exception e){
            Toast.makeText(this,"Please input your money!",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void openOne(View btn){
        openCfg();
    }

    private void openCfg() {
        Log.i(Tag, "openOne");
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(Tag, "openOne:dollarRate" + dollarRate);
        Log.i(Tag, "openOne:euroRate" + euroRate);
        Log.i(Tag, "openOne:wonRate" + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bdl = data.getExtras();
            dollarRate = bdl.getDouble("key_dollar", 0.0);
            euroRate = bdl.getDouble("key_euro", 0.0);
            wonRate = bdl.getDouble("key_won", 0.0);

            Log.i(Tag, "onActivityResult:dollarRate=" + dollarRate);
            Log.i(Tag, "onActivityResult:euroRate=" + euroRate);
            Log.i(Tag, "onActivityResult:wonRate=" + wonRate);

            //将新的汇率值保存到SP中
            SharedPreferences sharedPreferences=getSharedPreferences("myRate", Activity.MODE_PRIVATE);
            //SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putFloat("dollar_rate",(float) dollarRate);
            editor.putFloat("euro_rate",(float) euroRate);
            editor.putFloat("won_rate",(float) wonRate);
            editor.commit();

            Log.i(Tag,"onActivityResult:committing finished");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(Tag,"run:run()....");
        for(int i=1;i<=5;i++){
            Log.i(Tag,"run:i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //获取Message对象，用于返回主线程
        Message msg=handler.obtainMessage();
        msg.what=5;
        msg.obj="Hello from run()";
        handler.sendMessage(msg);

        //获取网络数据

        URL url= null;
        try {
            url = new URL("http://forex.hexun.com/rmbhl/#zkRate");
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            InputStream in =http.getInputStream();
            String html=inputStream2String(in);
            Log.i(Tag,"run:html="+html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String inputStream2String(InputStream in)throws IOException{
        StringBuffer out=new StringBuffer();
        byte [] b=new byte[4096];
        for(int n;(n=in.read(b))!=-1;){
            out.append(new String(b,0,n));
        }
        return out.toString();
    }

}
