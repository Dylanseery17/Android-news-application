package com.example.project_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListActivity extends AppCompatActivity {

    //GLOBAL VARIABLES
    private static final int REQUEST_CODE_DETAILS_ACTIVITY = 1234;
    ListView Listnames;
    String[] countrycodes = {"ae","ar","at","au","be","bg","br","ca","ch","cn","co","cu","cz","de","eg","fr","gb","gr","hk","hu","id","ie","il","in","it","jp","kr","lt","lv","ma","mx","my","ng","nl","no","nz","ph","pl","pt","ro","rs","ru","sa","se","sg","si","sk","th","tr","tw","ua","us","ve","za"};
    List<String> cc = Arrays.asList(countrycodes);
    ArrayList<NewsItem> List_news;
    NewsListAdapter adapter;
    String Countrycode;
    String Country;
    ArrayList<JSONObject> storeditems_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        Intent intent = new Intent(ListActivity.this, MapsActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_DETAILS_ACTIVITY);
                        break;
                }
                return true;
            }
        });

        // GETTING VALUES FROM MAP ACTIVITY
        Countrycode = getIntent().getStringExtra("countryCode");
        Country = getIntent().getStringExtra("country");

        TextView headline_textView = (TextView)  findViewById(R.id.headline);
        TextView country_textView = (TextView)  findViewById(R.id.country);
        country_textView.setText(Country + " , " + Countrycode);

        // IF API SUPPORTS THIS COUNTRY RUN THE QUERY WITH COUNTRY CODE ELSE GET AMERICAN NEWS
        if(cc.contains(Countrycode.toLowerCase())) {
            new Downloader().execute("https://newsapi.org/v2/top-headlines?country=" + Countrycode + "&apiKey=b64e3549cf0d491eb5668f19bf66ab3d");
        }else{
            Toast.makeText(getApplicationContext(),"Country not supported",Toast.LENGTH_LONG).show();
            new Downloader().execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=b64e3549cf0d491eb5668f19bf66ab3d");
        }
        //LIST VALUES AND SETTING IT
        List_news = new ArrayList<>();
        Listnames = (ListView) findViewById(R.id.NewsItems);
        adapter = new NewsListAdapter(this, R.layout.custom_list_layout, List_news);
//        adapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_list_item_1, List_names);
        Listnames.setAdapter(adapter);


        //REMOVING HEADER ON ROTATION
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            // show in same activity
            headline_textView.setVisibility(View.GONE);
            country_textView.setVisibility(View.GONE);
        }else{
            headline_textView.setVisibility(View.VISIBLE);
            country_textView.setVisibility(View.VISIBLE);
        }

        LinearLayout lin =  (LinearLayout) findViewById(R.id.Fragview);
        lin.setVisibility(View.GONE);

        //ON ITEM CLICK EITHER NEW ACTIVITY SHOWING FRAGMENT OR SHOW IN EXISTING ACTIVITY
        Listnames.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                JSONObject click = storeditems_list.get(position);

                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
                    // show in same activity

                    LinearLayout lin =  (LinearLayout) findViewById(R.id.Fragview);
                    lin.setVisibility(View.VISIBLE);
                    NewsActivity frag = (NewsActivity) getFragmentManager().findFragmentById(R.id.Fragitem);
                    frag.setNews(click);
                }else{
                    LinearLayout lin =  (LinearLayout) findViewById(R.id.Fragview);
                    lin.setVisibility(View.GONE);

                    try {
//                        NotificationHelper noti = new NotificationHelper(ListActivity.this);
//                        noti.createNotification( click.getString("title") ,  click.getString("description")  );

                        Intent intent = new Intent(ListActivity.this, NewsFragment.class);
                        intent.putExtra("jsonObj", click.toString());
                        startActivityForResult(intent, REQUEST_CODE_DETAILS_ACTIVITY);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }


        });
    }

    // SETTING LIST BACK TO EMPTY
    @Override
    public void onDestroy()
    {
        // Remove adapter reference from list
        Listnames.setAdapter(null);
        super.onDestroy();
    }

    // DOWNLOADS JSON VALUES DONE ON SEPARATE THREAD
    public class Downloader extends AsyncTask<String, Void, JSONObject> {

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
            JSONObject obj = result;
            try{

                JSONArray articles = obj.getJSONArray("articles");
                for(int i=0; i<articles.length(); i++) {
                    JSONObject c = articles.getJSONObject(i);

                    storeditems_list.add(c);

                    JSONObject source = c.getJSONObject("source");
                    String Ssource = source.getString("name");
                    String title = c.getString("title");
                    String img = c.getString("urlToImage");
                    // CREATING A NEWS OBJECT
                    NewsItem nwitem = new NewsItem(title, Ssource, img);
                    // ADDING THIS OBJECT TO OUR LIST
                    List_news.add(nwitem);

                    adapter.notifyDataSetChanged();
                }

                //RUNNING ALARM MANAGER SETTING IT TO RUN EVERY 10 MINUTES
                Intent intent = new Intent(ListActivity.this, BroadcastsReceiver.class);
                intent.putExtra("JSON_DATA",articles.toString());
                intent.putExtra("CountryCode",Countrycode);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        ListActivity.this, 234324243, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime(),
                        10*60*1000,
                        pendingIntent);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}