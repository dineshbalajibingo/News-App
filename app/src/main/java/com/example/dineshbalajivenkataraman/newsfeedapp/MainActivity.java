package com.example.dineshbalajivenkataraman.newsfeedapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItems>>, SwipeRefreshLayout.OnRefreshListener {
    private static int LOADER_ID = 0;
    SwipeRefreshLayout swipe;
    private NewsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isInternetConnectionAvailable()) {
            swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
            swipe.setOnRefreshListener(this);
            swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
            ListView listView = (ListView) findViewById(R.id.news_list_view);
            adapter = new NewsAdapter(this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    NewsItems news = adapter.getItem(i);
                    String url = news.url;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
        else
            Toast.makeText(MainActivity.this, R.string.error_no_internet,
                    Toast.LENGTH_SHORT).show();
    }
    private boolean isInternetConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    @Override
    public Loader<List<NewsItems>> onCreateLoader(int i, Bundle bundle) {
        return new ResponseLoader(this);
    }
    @Override
    public void onLoadFinished(Loader<List<NewsItems>> loader, List<NewsItems> news) {
        swipe.setRefreshing(false);
        if (news != null) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.setNotifyOnChange(true);
            adapter.addAll(news);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<NewsItems>> loader) {
    }
    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null,  this);
    }
}
