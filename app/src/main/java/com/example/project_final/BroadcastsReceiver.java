package com.example.project_final;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BroadcastsReceiver extends BroadcastReceiver{


    //   GLOBAL VARIABLES
    String Json;
    List<String> list;
    String CountryCode;
    int newvals=0;
    NotificationHelper noti;

//    BROADCAST RECEIVER RAN EVERY 10 MINUTES
    @Override
    public void onReceive(Context context, Intent intent) {

        // GETTING COUNTRY CODE FROM LIST ACTIVIY
        // GETTING LIST DATA FROM LIST ACTIVITY
        Json = intent.getStringExtra("JSON_DATA");
        CountryCode = intent.getStringExtra("CountryCode").toLowerCase();
        list =  new ArrayList<>();
        // SETTING UP NOTIFCATION
        noti = new NotificationHelper(context);
        // CALLING NEW LIST TWO COMPARE NEW LIST WITH OLD LIST
        new checker().execute("https://newsapi.org/v2/top-headlines?country="+CountryCode+"&apiKey=b64e3549cf0d491eb5668f19bf66ab3d");
    }


    // CHECKS FOR NEW LIST ITEMS
    public class checker extends AsyncTask<String, Void, JSONObject> {
        // DOWNLOADING NEW JSON DATA DONE ON SEPARATE THREAD
        @Override
        protected JSONObject doInBackground(String... urls){
            HttpURLConnection connection;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                connection.disconnect();

                return new JSONObject(responseStrBuilder.toString());

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // NEW JSON OBJECT
            JSONObject obj = result;
            try{
                JSONArray oldjarticles = new JSONArray(Json);
                JSONArray articles = obj.getJSONArray("articles");
                for(int i=0; i<articles.length(); i++) {
                    JSONObject c = articles.getJSONObject(i);
                    list.add(c.getString("title"));
                }

                // IF THE NEW OBJECT DOES NOT EXIST IN THE OLD ONE INCREMENT
                for(int i=0; i<oldjarticles.length(); i++) {
                    JSONObject c = oldjarticles.getJSONObject(i);
                    if(!list.contains(c.getString("title"))){
                        newvals++;
                    }
                }
                Log.d("id" , "" + newvals );
                // IF THERE IS NEW VALUES SEND NOTIFICATION IF NOT DON'T
                if(newvals>0) {
                    Json = articles.toString();
                    noti.createNotification("There are new " + newvals + " articles", "There are new " + newvals + " articles");
                    newvals = 0;
                }else{
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}

