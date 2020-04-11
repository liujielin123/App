package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {

    EditText rmb;
    TextView show;
    public final String Tag="RateActivity";
    private double dollarRate=1/6.7;
    private double euroRate=1/11.0;
    private double wonRate=500.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb=findViewById(R.id.rmb);
        show=findViewById(R.id.showOut);

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
        }
    }

    public void openOne(View btn){
        Log.i(Tag,"openOne");
        Intent config=new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        Log.i(Tag,"openOne:dollarRate"+dollarRate);
        Log.i(Tag,"openOne:euroRate"+euroRate);
        Log.i(Tag,"openOne:wonRate"+wonRate);

        //startActivity(config);
        startActivityForResult(config,1);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1&&resultCode==2){
            Bundle bdl=data.getExtras();
            dollarRate=bdl.getDouble("key_dollar",0.0);
            euroRate=bdl.getDouble("key_euro",0.0);
            wonRate=bdl.getDouble("key_won",0.0);

            Log.i(Tag,"onActivityResult:dollarRate="+dollarRate);
            Log.i(Tag,"onActivityResult:euroRate="+euroRate);
            Log.i(Tag,"onActivityResult:wonRate="+wonRate);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


}
