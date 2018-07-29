package com.example.jimison.xml_analysisdemo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ListView airList;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        airList = findViewById(R.id.airList);
        client = new OkHttpClient();

        getAirXml();
    }

    public ArrayList<HashMap<String,Object>> parse(InputStream stream){
        String tagName = null;
        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
        HashMap<String,Object> hashMap = new HashMap<>();
        int findCount = 0;
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(stream));
            int eventType = parser.getEventType();  //Event 就是xml的tag
            while (eventType != XmlPullParser.END_DOCUMENT){  //還未到達結束文件的tag
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:  //可寫可不寫，以下列出所有可能的選項
                        break;
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if(findCount==0 & tagName.equals("Data")) findCount++;
                        break;
                    case XmlPullParser.TEXT:
                        if(tagName.equals("SiteName")&hashMap.containsKey("SiteName")==false){
                            hashMap.put("SiteName",parser.getText());
                        }else if(tagName.equals("CO")&hashMap.containsKey("CO")==false){
                            hashMap.put("CO",parser.getText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if(tagName.equals("Data")){
                            findCount = 0;
                            arrayList.add(hashMap);
                            hashMap = new HashMap<>();
                        }
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = parser.next();
            }
            return arrayList;
        }catch (Exception e){
            e.printStackTrace();  //此方法會將錯誤訊息顯示在Log訊息中
        }
        return arrayList;
    }

    private void getAirXml(){
        Request request = new Request.Builder().url("http://opendata2.epa.gov.tw/AQI.xml").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)  //由StandardCharsets產生，因其最低API level = 19;
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                final InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name()));  //str.getBytes()能將字串轉為位元資料
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList data = parse(stream);
                        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),data,R.layout.item,new String[]{"SiteName","CO"},new int[]{R.id.siteName,R.id.pm25});
                        airList.setAdapter(adapter);
                    }
                });
            }
        });
    }
}
