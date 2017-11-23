package com.example.dineshbalajivenkataraman.newsfeedapp;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ResponseLoader extends AsyncTaskLoader<List<NewsItems>> {
    public ResponseLoader(Context context) {
        super(context);
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
    @Override
    public List<NewsItems> loadInBackground() {
        List<NewsItems> listOfNews = null;
        try {
            URL url = NetworkConnResponse.createUrl();
            String jsonResponse = NetworkConnResponse.makeHttpRequest(url);
            listOfNews = NetworkConnResponse.parseJson(jsonResponse);
        } catch (IOException e) {
            Log.e("Queryutils", "Error Loader LoadInBackground: ", e);
        }
        return listOfNews;
    }
}
