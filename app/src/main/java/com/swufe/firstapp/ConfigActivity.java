package com.swufe.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity {

    public final String Tag="ConfigActivity";
    EditText dollarText;
    EditText euroText;
    EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Intent config=getIntent();
        Double dollar_rate=config.getDoubleExtra("dollar_rate_key",0.0);
        Double euro_rate=config.getDoubleExtra("euro_rate_key",0.0);
        Double won_rate=config.getDoubleExtra("won_rate_key",0.0);

        Log.i(Tag,"onCreate:dollar_rate="+dollar_rate);
        Log.i(Tag,"onCreate:euro_rate="+euro_rate);
        Log.i(Tag,"onCreate:won_rate="+won_rate);

        dollarText=findViewById(R.id.dollar_rate);
        euroText=findViewById(R.id.euro_rate);
        wonText=findViewById(R.id.won_rate);

        //显示数据到控件
        dollarText.setText(String.format("%.2f",dollar_rate));
        euroText.setText(String.format("%.2f",euro_rate));
        wonText.setText(String.format("%.2f",won_rate));

    }

    public void save(View btn){

        try {
            //获取新的值
            double newDollar_rate=Double.valueOf(dollarText.getText().toString());
            double newEuro_rate=Double.valueOf(euroText.getText().toString());
            double newWon_rate=Double.valueOf(wonText.getText().toString());

            Log.i(Tag,"save:newDollar_rate="+newDollar_rate);
            Log.i(Tag,"save:newEuro_rate="+newEuro_rate);
            Log.i(Tag,"save:newWon_rate="+newWon_rate);

            //保存到Bundle
            Intent intent=getIntent();
            Bundle bundle=new Bundle();
            bundle.putDouble("key_dollar",newDollar_rate);
            bundle.putDouble("key_euro",newEuro_rate);
            bundle.putDouble("key_won",newWon_rate);
            intent.putExtras(bundle);
            setResult(2,intent);

            //返回到调用页面
            finish();
        }
        catch (Exception e){
            Toast.makeText(this,"Error!Please try again",Toast.LENGTH_SHORT).show();
        }

    }
}
