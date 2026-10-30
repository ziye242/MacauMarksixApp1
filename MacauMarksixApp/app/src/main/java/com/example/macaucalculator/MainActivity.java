package com.example.macaucalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
 private final ExecutorService io=Executors.newSingleThreadExecutor();
 private final String latestUrl="https://macaumarksix.com/api/macaujc2.com";
 private final String historyBase="https://history.macaumarksix.com/history/macaujc2/y/";
 private LinearLayout list; private TextView status; private JSONArray records=new JSONArray();
 private static final int[] TRACK={3,4,5,6,7,6,5,4};
 private static final String[] ZODIACS={"马","蛇","龙","兔","虎","牛","鼠","猪","狗","鸡","猴","羊"};
 private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 @Override public void onCreate(Bundle b){super.onCreate(b);build();fetch();}
 private void build(){LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(0xfff3f5f8);
  TextView head=new TextView(this);head.setText("六合彩计算器");head.setTextSize(24);head.setTypeface(null,Typeface.BOLD);head.setTextColor(-1);head.setPadding(dp(18),dp(20),dp(18),dp(18));head.setBackgroundColor(0xff124d7a);root.addView(head);
  LinearLayout bar=new LinearLayout(this);Button refresh=new Button(this);refresh.setText("刷新历史数据");refresh.setOnClickListener(v->fetch());bar.addView(refresh,new LinearLayout.LayoutParams(0,dp(48),1));Button calc=new Button(this);calc.setText("+03往返计算");calc.setOnClickListener(v->showCalculation());bar.addView(calc,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(bar);
  status=new TextView(this);status.setPadding(dp(14),dp(8),dp(14),dp(8));root.addView(status);ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);list.setPadding(dp(12),dp(5),dp(12),dp(20));scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);}
 private void fetch(){status.setText("正在获取最新开奖和历史数据…");io.execute(()->{try{int year=Calendar.getInstance().get(Calendar.YEAR);JSONArray all=new JSONArray();for(int y=year;y>=year-5;y--){try{JSONArray a=extract(get(historyBase+y));for(int i=0;i<a.length();i++)all.put(a.getJSONObject(i));}catch(Exception ignored){}}JSONObject latest=first(get(latestUrl));if(latest!=null)all.put(0,latest);JSONArray unique=dedupe(all);runOnUiThread(()->{records=unique;showRecords();status.setText("读取到 "+records.length()+" 条记录（当前展示最近40期）");});}catch(Exception e){runOnUiThread(()->status.setText("获取失败："+e.getMessage()+"。请检查网络后重试。"));}});}
 private JSONArray dedupe(JSONArray in){JSONArray out=new JSONArray();Set<String> seen=new HashSet<>();for(int i=0;i<in.length();i++){JSONObject o=in.optJSONObject(i);if(o!=null&&seen.add(o.optString("expect")))out.put(o);}return out;}
 private JSONObject first(String s)throws Exception{Object x=new JSONTokener(s).nextValue();if(x instanceof JSONArray)return ((JSONArray)x).optJSONObject(0);JSONObject o=(JSONObject)x;if(o.has("openCode"))return o;JSONArray a=o.optJSONArray("data");return a==null?null:a.optJSONObject(0);}
 private JSONArray extract(String s)throws Exception{Object x=new JSONTokener(s).nextValue();if(x instanceof JSONArray)return (JSONArray)x;JSONObject o=(JSONObject)x;for(String k:new String[]{"data","history","list","result"}){JSONArray a=o.optJSONArray(k);if(a!=null)return a;}JSONArray a=new JSONArray();if(o.has("openCode"))a.put(o);return a;}
 private String get(String url)throws Exception{HttpURLConnection c=(HttpURLConnection)new URL(url).openConnection();c.setConnectTimeout(15000);c.setReadTimeout(15000);c.setRequestProperty("Accept","application/json");try{int code=c.getResponseCode();InputStream in=code>=200&&code<300?c.getInputStream():c.getErrorStream();ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)b.write(buf,0,n);if(code<200||code>=300)throw new IOException("HTTP "+code);return b.toString("UTF-8");}finally{c.disconnect();}}
 private String[] nums(JSONObject o){String[] a=o.optString("openCode","").split(",");return a.length==7?a:null;}
 private int plus3(int n){return ((n-1+3)%49)+1;}
 private String zodiac(int n){return ZODIACS[(n-1)%12];}
 private void showRecords(){list.removeAllViews();title("最新及历史开奖记录");for(int i=0;i<Math.min(40,records.length());i++){JSONObject o=records.optJSONObject(i);if(o==null)continue;addText("第"+o.optString("expect")+"期  "+o.optString("openTime")+"\n号码："+o.optString("openCode")+"\n波色："+o.optString("wave")+"\n生肖："+o.optString("zodiac"));}}
 private void showCalculation(){list.removeAllViews();title("+03往返位置计算");addText("位置轨迹：3→4→5→6→7→6→5→4，循环重复。\n每次取对应位置号码，按01—49循环加03。\n生肖映射：2026从马开始，按号码递增循环12生肖。");for(int i=0;i<records.length();i++){JSONObject o=records.optJSONObject(i);if(o==null)continue;String[] a=nums(o);if(a==null)continue;StringBuilder s=new StringBuilder("第").append(o.optString("expect")).append("期：\n");for(int p:TRACK){try{int original=Integer.parseInt(a[p-1].trim());int result=plus3(original);s.append("第").append(p).append("位 ").append(String.format(Locale.US,"%02d",original)).append(" +03 = ").append(String.format(Locale.US,"%02d",result)).append("（").append(zodiac(result)).append("）\n");}catch(Exception ignored){}}addText(s.toString());}}
 private void title(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(19);t.setTypeface(null,1);t.setTextColor(0xff124d7a);t.setPadding(dp(5),dp(12),dp(5),dp(8));list.addView(t);}
 private void addText(String s){TextView t=new TextView(this);t.setText(s);t.setTextSize(14);t.setTextColor(0xff263238);t.setPadding(dp(12),dp(10),dp(12),dp(10));t.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(4),0,dp(4));list.addView(t,p);}
 @Override protected void onDestroy(){super.onDestroy();io.shutdownNow();}
}
