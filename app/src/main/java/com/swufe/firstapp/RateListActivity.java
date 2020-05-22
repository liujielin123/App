package com.swufe.firstapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{

    List<String> dataList;
    Handler handler;
    public final String Tag="RateListActivity";
    private String logDate="";
    private final String DATE_SP_KEY="lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);

        /*dataList=new ArrayList<String>();
        for (int i=0;i<100;i++){
            dataList.add("item"+i);
        }*/

        SharedPreferences sp=getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate=sp.getString(DATE_SP_KEY,"");
        Log.i(Tag,"onCreate:lastRateDateStr="+logDate);

        String data[]={"wait....."};
        ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);

        //开启子线程
        Thread thread=new Thread(this);
        thread.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7){
                    dataList= (List<String>) msg.obj;
                    ListAdapter adapter=new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,dataList);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        Log.i(Tag,"run:run()....");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> retList=new ArrayList<String>();

        String curDateStr=(new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i(Tag,"run:curDateStr="+curDateStr+" logDate="+logDate);
        if(curDateStr.equals(logDate)){
            //如果相等，则不从网络中获取数据
            Log.i(Tag,"run:日期相等，从数据库中获取数据");
            RateManager manager= new RateManager(this);
            for(RateItem item:manager.listAll()){
                retList.add(item.getCurName()+"==>"+item.getCurRate());
            }
        }
        else {
            Log.i(Tag,"run:日期不相等，从数据库中获取数据");
            //获取网络数据，放入List带回到主线程中
            Document doc = null;
            List<RateItem> rateList=new ArrayList<RateItem>();
            try {
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                Log.i(Tag,"run:"+doc.title());
                //获取td中的数据
                Elements tds=doc.getElementsByTag("td");
            /*for(Element td:tds){
                Log.i(Tag,"run:"+td);
            }*/
                String td1,td2;
                for(int i=0;i<tds.size();i+=6){
                    td1=tds.get(i).text();//货币名称
                    td2=tds.get(i+5).text();//td1对应的汇率
                    Log.i(Tag,"run:"+td1+"==>"+td2);
                    retList.add(td1+"==>"+td2);
                    rateList.add(new RateItem(td1,td2));
                }
                //把数据写入数据库
                RateManager manager= new RateManager(this);
                manager.deleteAll();
                manager.addAll(rateList);

                //记录更新日期
                SharedPreferences sp=getSharedPreferences("myrate", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=sp.edit();
                edit.putString(DATE_SP_KEY,curDateStr);
                edit.commit();
                Log.i(Tag,"run:更新日期："+curDateStr);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(Tag,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码");
            }
        }
        //获取Message对象，用于返回主线程
        Message msg=handler.obtainMessage(7);
        msg.obj=retList;
        handler.sendMessage(msg);

    }
}
