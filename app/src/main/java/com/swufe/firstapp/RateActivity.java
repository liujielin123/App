package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateActivity extends AppCompatActivity {

    EditText rmb;
    TextView show;

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
                show.setText(String.format("%.2f",rmb/6.7));
            }else if(btn.getId()==R.id.btn_euro){
                show.setText(String.format("%.2f",rmb/11));
            }else {
                show.setText(String.format("%.2f",rmb*500));
            }
        }
        catch (Exception e){
            Toast.makeText(this,"Please input your money!",Toast.LENGTH_SHORT).show();
        }
    }
}
