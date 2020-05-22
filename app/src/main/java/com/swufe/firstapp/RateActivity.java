package com.swufe.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {

    EditText rmb;
    TextView show;
    Handler handler;
    public final String Tag="RateActivity";
    private double dollarRate;
    private double euroRate;
    private double wonRate;
    private String updateRate;//构建汇率更新字符串，格式："时间(年.月.日)"+":"+"true/false"(true表示当日已更新，false表示当日未更新)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb=findViewById(R.id.rmb);
        show=findViewById(R.id.showOut);

        //获取SP中的数据
        SharedPreferences sp=getSharedPreferences("myRate", Activity.MODE_PRIVATE);
        //SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        updateRate=sp.getString("update_rate","0000.00.00:false");
        dollarRate=sp.getFloat("dollar_rate",0.0f);
        euroRate=sp.getFloat("euro_rate",0.0f);
        wonRate=sp.getFloat("won_rate",0.0f);

        Log.i(Tag,"onCreate:updateRate="+updateRate);
        Log.i(Tag,"onCreate:dollarRate="+dollarRate);
        Log.i(Tag,"onCreate:euroRate="+euroRate);
        Log.i(Tag,"onCreate:wonRate="+wonRate);

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");//设置日期格式
        String OSTime=df.format(new Date());//获取系统当前时间

        Log.i(Tag,"onCreate:OSTime="+OSTime);

        //检查上次更新日期
        if(!OSTime.equals(updateRate.substring(0,10))){
            updateRate=OSTime+":false";
        }

        //检查当日是否更新，若未更新，则开启子线程完成更新，并将数据保存至sp
        if(updateRate.substring(11).equals("false")){
            //开启子线程
            Thread thread=new Thread(this);
            thread.start();
        }
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5){
                    Bundle bundle=(Bundle) msg.obj;
                    dollarRate=bundle.getFloat("dollarRate",0.0f);
                    euroRate=bundle.getFloat("euroRate",0.0f);
                    wonRate=bundle.getFloat("wonRate",0.0f);

                    //将新的汇率值保存到SP中
                    SharedPreferences sp=getSharedPreferences("myRate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("update_rate",updateRate.replace("false","true"));
                    editor.putFloat("dollar_rate",(float) dollarRate);
                    editor.putFloat("euro_rate",(float) euroRate);
                    editor.putFloat("won_rate",(float) wonRate);
                    editor.commit();

                    Log.i(Tag,"onActivityResult:handlerMessage:committing of rate finished");

                    Log.i(Tag,"onCreate:handlerMessage"+updateRate);
                    Log.i(Tag,"onCreate:handlerMessage:dollarRate="+dollarRate);
                    Log.i(Tag,"onCreate:handlerMessage:euroRate="+euroRate);
                    Log.i(Tag,"onCreate:handlerMessage:wonRate="+wonRate);

                    Toast.makeText(RateActivity.this,"Rates has updated",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.menu_set){
            openCfg();
        }
        else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            /*Intent list = new Intent(this, RateList2Activity.class);
            startActivityForResult(list, 1);*/
            //测试数据
            /*RateItem item_rate=new RateItem("a","10");
            RateManager manager=new RateManager(this);
            manager.add(item_rate);
            manager.add(new RateItem("b","20"));
            Log.i(Tag,"onOptionsItemSelected:写入数据库");

            //查询所有数据
            List<RateItem> testList=manager.listAll();
            for(RateItem i:testList){
                Log.i(Tag,"onOptionsItemSelected:取出数据[id="+i.getId()+"]Name="+i.getCurName()+" Rate="+i.getCurRate());
            }*/
            Intent list = new Intent(this, RateListActivity.class);
            startActivityForResult(list, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View btn){
        try {
            String r=rmb.getText().toString();
            double rmb=Double.valueOf(r);
            if (btn.getId()==R.id.btn_dollar){
                show.setText(String.format("%.2f",rmb*dollarRate));
            }else if(btn.getId()==R.id.btn_euro){
                show.setText(String.format("%.2f",rmb*euroRate));
            }else {
                show.setText(String.format("%.2f",rmb*wonRate));
            }
        }
        catch (Exception e){
            Toast.makeText(this,"Please input your money!",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void openOne(View btn){
        openCfg();
    }

    private void openCfg() {
        Log.i(Tag, "openOne");
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(Tag, "openOne:dollarRate" + dollarRate);
        Log.i(Tag, "openOne:euroRate" + euroRate);
        Log.i(Tag, "openOne:wonRate" + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bdl = data.getExtras();
            dollarRate = bdl.getDouble("key_dollar", 0.0);
            euroRate = bdl.getDouble("key_euro", 0.0);
            wonRate = bdl.getDouble("key_won", 0.0);

            Log.i(Tag, "onActivityResult:dollarRate=" + dollarRate);
            Log.i(Tag, "onActivityResult:euroRate=" + euroRate);
            Log.i(Tag, "onActivityResult:wonRate=" + wonRate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(Tag,"run:run()....");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //用于保存从网络获取的汇率
        Bundle bdl_rate=new Bundle();

        //获取网络数据
        //方法1
        /*URL url= null;
        try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            InputStream in =http.getInputStream();
            String html=inputStream2String(in);
            Log.i(Tag,"run:html="+html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //方法2
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

                try{
                    if("美元".equals(td1.text())){ bdl_rate.putFloat("dollarRate",100f/Float.valueOf(td2.text()));}
                    else if("欧元".equals(td1.text())){ bdl_rate.putFloat("euroRate",100f/Float.valueOf(td2.text()));}
                    else if("韩元".equals(td1.text())){ bdl_rate.putFloat("wonRate",100f/Float.valueOf(td2.text()));}
                }
                catch (Exception ee){
                    Log.i(Tag,"run:网页已改变，请修改解析网页源代码");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Message对象，用于返回主线程
        Message msg=handler.obtainMessage();
        msg.what=5;
        //msg.obj="Hello from run()";
        msg.obj=bdl_rate;
        handler.sendMessage(msg);
    }

    private String inputStream2String(InputStream in)throws IOException{
        StringBuffer out=new StringBuffer();
        byte [] b=new byte[4096];
        for(int n;(n=in.read(b))!=-1;){
            out.append(new String(b,0,n));
        }
        return out.toString();
    }

}
