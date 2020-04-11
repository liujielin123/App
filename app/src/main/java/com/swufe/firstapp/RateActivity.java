package com.swufe.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class RateActivity extends AppCompatActivity {

    EditText rmb;
    TextView show;
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

}
