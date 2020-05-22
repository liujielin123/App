package com.swufe.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RateList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    Handler handler;
    private List<HashMap<String,String>> listItems;//存放文字、图片信息
    private SimpleAdapter listItemAdapter;//适配器
    private static final String Tag="RateList2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.initListView();

        //this.setListAdapter(listItemAdapter);
        //自定义Adapter
        /*MyAdapter myAdapter=new MyAdapter(this,R.layout.list_item,listItems);
        this.setListAdapter(myAdapter);*/

        //开启子线程
        Thread thread=new Thread(this);
        thread.start();
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7){
                    listItems= (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter=new SimpleAdapter(RateList2Activity.this,listItems,//listItems数据簿
                            R.layout.list_item,//listItem的XML布局实现
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    private void initListView(){
        listItems=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<String, String>();
            map.put("ItemTitle","Rate:"+i);//标题文字
            map.put("ItemDetail","detail"+i);//详细描述
            listItems.add(map);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter=new SimpleAdapter(this,listItems,//listItems数据簿
                R.layout.list_item,//listItem的XML布局实现
                new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail}
                );
    }

    public void run() {
        Log.i(Tag,"run:run()....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取网络数据，放入List带回到主线程中
        List<HashMap<String,String>> retList=new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(Tag,"run:"+doc.title());
            //获取td中的数据
            Elements tds=doc.getElementsByTag("td");
            /*for(Element td:tds){
                Log.i(Tag,"run:"+td);
            }*/
            Element td1,td2;
            for(int i=0;i<tds.size();i+=6){
                td1=tds.get(i);//货币名称
                td2=tds.get(i+5);//td1对应的汇率
                Log.i(Tag,"run:"+td1.text()+"==>"+td2.text());
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("ItemTitle",td1.text());
                map.put("ItemDetail",td2.text());
                retList.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(Tag,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码");
        }

        //获取Message对象，用于返回主线程
        Message msg=handler.obtainMessage(7);
        msg.obj=retList;
        handler.sendMessage(msg);

    }

    //实现监听方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(Tag,"onItemClick:parent="+parent);
        Log.i(Tag,"onItemClick:view="+view);
        Log.i(Tag,"onItemClick:position="+position);
        Log.i(Tag,"onItemClick:id="+id);

        HashMap<String,String> map=(HashMap<String, String>) getListView().getItemAtPosition(position);
        //String title=map.get("ItemTitle");
        //String detail=map.get("ItemDetail");

        TextView titleView=view.findViewById(R.id.itemTitle);
        TextView detailView=view.findViewById(R.id.itemDetail);
        String title=String.valueOf(titleView.getText());
        String detail=String.valueOf(detailView.getText());

        Log.i(Tag,"onItemClick:titleStr="+title);
        Log.i(Tag,"onItemClick:detailStr="+detail);

        //打开新的页面，并且传递参数
        Intent rateCall=new Intent(this,RateCallActivity.class);
        rateCall.putExtra("currency",title);
        rateCall.putExtra("rate",Float.valueOf(detail));
        startActivity(rateCall);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(Tag,"onItemLongClick:长按列表项position="+position);
        //删除操作
        /*listItems.remove(position);
        listItemAdapter.notifyDataSetChanged();*/
        //构造对话框进行确认操作
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除当前数据？").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(Tag,"onItemLongClick：对话框事件处理");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("否",null);
        builder.create().show();
        Log.i(Tag,"onItemLongClick:size="+listItems.size());
        return true;//返回true长按触发后会屏蔽短按触发，false则反之
    }
}
