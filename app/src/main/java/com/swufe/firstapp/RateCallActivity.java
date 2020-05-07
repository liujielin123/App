package com.swufe.firstapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RateCallActivity extends AppCompatActivity {

    float rate;
    EditText in;
    private final String TAG="RateCallActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_call);

        rate=getIntent().getFloatExtra("rate",0.0f);
        String currency=getIntent().getStringExtra("currency");

        Log.i(TAG,"onCreate:currency="+currency);
        Log.i(TAG,"onCreate:rate="+rate);

        ((TextView)findViewById(R.id.currency)).setText(currency);
        in=findViewById(R.id.rmb2);
        in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextView show=RateCallActivity.this.findViewById(R.id.showOut2);
                if(s.length()>0){
                    float val=Float.parseFloat(s.toString());
                    show.setText(val+"RMB==>"+(100/rate*val));
                }
                else {
                    show.setText("");
                }
            }
        });

    }
}
