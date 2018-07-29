# XML_AnalysisDemo

![image](https://github.com/Jimison-TW/AndroidDemo03-OpenDataAnalysis/blob/master/device-2018-07-27-152922.png?raw=true)

## 開發版本
Andorid 3.1.2 </br>
SdkVersion 27 </br>
minSdkVersion 15 </br>
targetSdkVersion 27 </br>

## 學習重點
1. 除了Json格式的解析，第二個物件解析格式則是Xml文件
2. App頁面的切換，與手動從Manifest修改App的首頁畫面
3. ListView的用法與顯示方式

## Xml如何解析
1. 同樣利用Okhttp取得OpenData的資料，並將接收到的文字內容以2進位的資料形式，用ByteArrayInputStream轉換成InputStream
```java=
final InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name()));  //str.getBytes()能將字串轉為位元資料
```

2. xml具有兩種形式
一種是把值寫在tag所夾住的內容中`<SiteName>基隆</SiteName>`，範例在MainActivity
一種是把值寫在xml的屬性中`<Route ID="1431" ProviderId="10350" nameZh="0北" ddesc="金獅湖站－金獅湖站" departureZh="金獅湖站" destinationZh="捷運鹽埕埔站" />`範例在BusAcitvity中


2. 用`org.xmlpull.v1.XmlPullParserFactory`與`org.xmlpull.v1.XmlPullParser`這兩個物件來進行解析</br>
依照不同的xml Tag來將Tag內的內容解析出來
```java=
//此範例為把值寫在tag所夾住的內容中
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
```

```java=
//此範例是把值寫在xml的屬性中
try {
    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    XmlPullParser parser = factory.newPullParser();
    parser.setInput(new InputStreamReader(stream));
    int eventType = parser.getEventType();  //Event 就是xml的tag
    while (eventType != XmlPullParser.END_DOCUMENT) {  //還未到達結束文件的tag
        switch (eventType) {
            case XmlPullParser.START_DOCUMENT:  //可寫可不寫，以下列出所有可能的選項
                break;
            case XmlPullParser.START_TAG:
                tagName = parser.getName();
                if (tagName.equals("Route") & findCount == 0) {
                    findCount++;
                    hashMap.put("ID", parser.getAttributeValue(null, "ID"));
                    hashMap.put("nameZh", parser.getAttributeValue(null, "nameZh"));
                    hashMap.put("ddesc", parser.getAttributeValue(null, "ddesc"));
                    arrayList.add(hashMap);
                    hashMap = new HashMap<>();
                }
                break;
            case XmlPullParser.TEXT:
                break;
            case XmlPullParser.END_TAG:
                tagName = parser.getName();
                if (tagName.equals("Route")) findCount = 0;
                break;
            case XmlPullParser.END_DOCUMENT:
                break;
        }
        eventType = parser.next();
    }
    return arrayList;
} catch (Exception e) {
    e.printStackTrace();  //此方法會將錯誤訊息顯示在Log訊息中
}
return arrayList;
```

3. 不同Tag的內容解析，大概是像這樣的結構<START_TAG>TEXT</END_TAG>
* START_DOCUMENT：文件內容一開始的第一個Tag
* START_TAG：開始的tag，會和另一個具有反斜線的Tag成對
* TEXT：在<START_TAG>與</END_TAG>包含的內容
* END_TAG：結束的Tag，具有反斜線的Tag
* END_DOCUMENT：結束文件的最後一個Tag