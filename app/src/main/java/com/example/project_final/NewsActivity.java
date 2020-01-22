package com.example.project_final;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsActivity extends Fragment {

    private String url ="";
    //MIX UP WITH NAMES THIS SHOULD BE NEWS FRAGMENT
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_display, container, false);
    }

    /*
     * This method is called after the containing activity is done being created.
     * This is a good place to attach event listeners and do other initialization.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setNews(JSONObject newsitem) {
        JSONObject obj = newsitem;

        TextView DisplayTitle = (TextView)  getActivity().findViewById(R.id.title);
        TextView DisplayAuthor = (TextView)  getActivity().findViewById(R.id.author);
        TextView DisplayDate = (TextView)  getActivity().findViewById(R.id.date);
        TextView DisplayDescription = (TextView)  getActivity().findViewById(R.id.desc);
        TextView DisplayContent = (TextView)  getActivity().findViewById(R.id.content);

        try{
            String title = obj.getString("title");
            String author = obj.getString("author");
            String strDate = obj.getString("publishedAt");
            String description = obj.getString("description");
            String content = obj.getString("content");
            url = obj.getString("url");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date dateStr = formatter.parse(strDate);
            SimpleDateFormat nformatter = new SimpleDateFormat("dd/MM/YYYY");

            String formattedDate = nformatter.format(dateStr);

            DisplayTitle.setText(title);
            DisplayAuthor.setText(author);
            DisplayDate.setText(formattedDate);
            DisplayDescription.setText(description);
            DisplayContent.setText(content);

            Button clickButton = (Button) getActivity().findViewById(R.id.Webview);
            clickButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
