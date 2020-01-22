package com.example.project_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NewsFragment extends AppCompatActivity {

    ImageView DisplayContent;
    //MIX UP WITH NAMES THIS SHOULD BE NEWS ACTIVITY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);


        Intent intent = this.getIntent();
        DisplayContent = (ImageView)  findViewById(R.id.imageView);

        // pull out the turtle ID to show from the activity's intent
        try{
            JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("jsonObj"));
            NewsActivity frag = (NewsActivity) getFragmentManager().findFragmentById(R.id.Fragitem2);
            frag.setNews(jsonObj);
            new Downloader().execute(jsonObj.getString("urlToImage"));
        }catch (Exception e){

        }
    }


    public class Downloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls){
            HttpsURLConnection connection;
            try {
                URL url = new URL(urls[0]);

                connection = (HttpsURLConnection) url.openConnection();
                connection.setUseCaches(true);
                connection.connect();

                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                connection.disconnect();

                return myBitmap;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if( result != null) {
                DisplayContent.setImageBitmap(result);
            }
        }

    }

}
