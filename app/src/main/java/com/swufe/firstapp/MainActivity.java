package com.swufe.firstapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add something
        TextView out=(TextView)findViewById(R.id.showText);
        EditText in=(EditText)findViewById(R.id.inputText);
        Button conv=(Button)findViewById(R.id.convert);
        conv.setOnClickListener(this);

    }
    public void onClick(View v){
        TextView out=(TextView)findViewById(R.id.showText);
        EditText in=(EditText)findViewById(R.id.inputText);
        try{
            double tem=Double.valueOf(in.getText().toString());
            out.setText("result:"+(tem*1.8+32));
        }
        catch (Exception e){
            out.setText("result:error!!!please try again!");
        }
    }


}
