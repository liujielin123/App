package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    TextView scoreA;
    TextView scoreB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        scoreA=findViewById(R.id.scoreA);
        scoreB=findViewById(R.id.scoreB);
    }

    public void btnAddA1(View v){ showScore(1,scoreA); }
    public void btnAddA2(View v){ showScore(2,scoreA); }
    public void btnAddA3(View v){ showScore(3,scoreA); }
    public void btnAddB1(View v){ showScore(1,scoreB); }
    public void btnAddB2(View v){ showScore(2,scoreB); }
    public void btnAddB3(View v){ showScore(3,scoreB); }
    public void btnReset(View v){
        scoreA.setText("0");
        scoreB.setText("0");
    }

    private void showScore(int inc,TextView score){
        Log.i("show","inc="+inc);
        String oldScore=(String) score.getText();
        score.setText(""+(Integer.valueOf(oldScore)+inc));
    }
}
