package com.swufe.firstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    TextView scoreA;
    TextView scoreB;
    private final String Tag="SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        scoreA=findViewById(R.id.scoreA);
        scoreB=findViewById(R.id.scoreB);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.i(Tag,"onSaveInstanceState:");

        //保存横屏或竖屏状态的数据
        outState.putString("teamA_score",scoreA.getText().toString());
        outState.putString("teamB_score",scoreB.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(Tag,"onRestoreInstanceState：");

        //还原onSaveInstanceState保存的数据
        scoreA.setText(savedInstanceState.getString("teamA_score"));
        scoreB.setText(savedInstanceState.getString("teamB_score"));
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
