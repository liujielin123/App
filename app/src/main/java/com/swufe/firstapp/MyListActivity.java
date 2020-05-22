package com.swufe.firstapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<String> data= new ArrayList<String>();
    private static final String TAG="MyListActivity";
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        //GridView listView=findViewById(R.id.myList);
        ListView listView=findViewById(R.id.myList);
        //init data
        for(int i=0;i<10;i++){
            data.add("item"+i);
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.nondata));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"ononItemClick:position="+position);
        Log.i(TAG,"ononItemClick:parent="+parent);
        adapter.remove(parent.getItemAtPosition(position));
        //adapter.notifyDataSetChanged();
    }
}
