package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SwufeInfoActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener{

    private static final String Tag="SwufeInfoActivity";
    private EditText in;
    private ListView out;
    private String data[];
    private Handler handler;
    private SimpleAdapter listItemAdapter;//适配器
    private String updateTime;//构建汇率更新字符串，表示上次更新时间，格式："年.月.日";
    private boolean flag=true;//需要更新则为true
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swufe_info);

        //获取SP中的数据
        SharedPreferences sp=getSharedPreferences("mySwufeData", Activity.MODE_PRIVATE);
        updateTime=sp.getString("updateTime","1970.01.01");//获取上次更新的时间
        int count=sp.getInt("recordCount",0);//获取公告数
        data=new String[count];
        for(int i=0;i<count;i++){
            data[i]=sp.getString(""+i,"");
            Log.i(Tag,"onCreate:data:"+data[i].toString());
        }
        Log.i(Tag,"onCreate:updateRate="+updateTime);

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");//设置日期格式
        final String OSTime=df.format(new Date());//获取系统当前时间
        Log.i(Tag,"onCreate:OSTime="+OSTime);

        //检查当周是否需要更新，若需要更新，则更新flag为false
        try {
            Date update=df.parse(updateTime);
            Calendar rightNow =Calendar.getInstance();;
            rightNow.setTime(update);
            for(int n=0;n<7;n++){
                if(df.format(rightNow.getTime()).equals(OSTime)){
                    flag=false;
                }
                rightNow.add(Calendar.DAY_OF_YEAR,1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //检查flag，若flaf为true，则开启子线程完成更新，并将数据保存至sp
        if(flag){
            //开启子线程
            Thread thread=new Thread(this);
            thread.start();
        }
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    Vector<String> data_list=(Vector<String>) msg.obj;

                    //将新的汇率值保存到SP中
                    SharedPreferences sp=getSharedPreferences("mySwufeData", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    data=new String[data_list.size()];
                    for(int m=0;m<data_list.size();m++){
                        data[m]=data_list.get(m);
                        editor.putString(""+m,data_list.get(m));
                    }
                    editor.putInt("recordCount",data_list.size());
                    editor.putString("updateTime",OSTime);
                    editor.commit();

                    Log.i(Tag,"onActivityResult:handlerMessage:updateTime="+OSTime);
                    Log.i(Tag,"onActivityResult:handlerMessage:committing of rate finished");
                    Toast.makeText(SwufeInfoActivity.this,"Data has updated",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
        out=findViewById(R.id.resultList);
        in=findViewById(R.id.keyWord);
        in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                List<HashMap<String,String>> dataList=new ArrayList<HashMap<String, String>>();
                int flag=0;
                for(int j=0;j<data.length;j++){
                    if(data[j].indexOf(s.toString())!=-1){
                        flag=1;
                        HashMap<String,String> map=new HashMap<String,String>();
                        map.put("ItemTitle",data[j].substring(0,data[j].indexOf("#")));
                        map.put("ItemDetail",data[j].substring(data[j].indexOf("#")+1));
                        dataList.add(map);
                    }
                }
                if(flag==1){
                    listItemAdapter=new SimpleAdapter(SwufeInfoActivity.this,dataList,//listItems数据簿
                            R.layout.list_item,//listItem的XML布局实现
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail}
                    );
                    out.setAdapter(listItemAdapter);
                    out.setOnItemClickListener(SwufeInfoActivity.this);
                }
                else {
                    Toast.makeText(SwufeInfoActivity.this,"Sorry!No information containing the keyword ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void run() {
        Log.i(Tag,"run:run()....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取网络数据，放入List带回到主线程中
        Document doc = null;
        Vector<String> list=new Vector<String>();

        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(Tag,"run:"+doc.title());
            //获取a中的数据
            Elements as=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
            Elements td=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > div > table > tbody > tr > td > table > tbody > tr")).select("td");
            int siteNum=Integer.parseInt((td.get(0).text()).substring(td.get(0).text().indexOf("/")+1));
            int count=0;
            Log.i(Tag,siteNum+"");
            for(int i=0;i<as.size();i++){
                list.add(count,as.get(i).attr("title")+"#https://it.swufe.edu.cn"+(as.get(i).attr("href")).replace("..",""));
                Log.i(Tag,list.get(count));
                count++;
            }
            for(int j=siteNum-1;j>0;j--){
                doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg/"+j+".htm").get();
                Elements temp=(doc.select("body > div.main > div > div > div.col-xs-12.col-md-9 > div > ul")).select("a");
                for(int k=0;k<temp.size();k++){
                    list.add(count,temp.get(k).attr("title")+"#https://it.swufe.edu.cn"+(temp.get(k).attr("href")).replace("..",""));
                    Log.i(Tag,list.get(count)+j);
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(Tag,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码");
        }

        //获取Message对象，用于返回主线程
        Message msg=handler.obtainMessage(7);
        msg.what=5;
        //msg.obj="Hello from run()";
        msg.obj=list;
        handler.sendMessage(msg);

    }

    //实现监听方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map=(HashMap<String, String>) out.getItemAtPosition(position);
        String site=map.get("ItemDetail");

        //打开浏览器
        Intent intent = new Intent();
        intent.setData(Uri.parse(site));//Url 就是你要打开的网址
        intent.setAction(Intent.ACTION_VIEW);
        this.startActivity(intent); //启动浏览器
    }
}

