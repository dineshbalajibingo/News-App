package com.example.dineshbalajivenkataraman.newsfeedapp;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class NetworkConnResponse {
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_WEB_TITLE= "webTitle";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String KEY_PUBLICATION_DATE = "webPublicationDate";
    private static final String KEY_SECTION_NAME = "sectionName";
    private static String KEY_TAGS= "tags";
    static String createStringUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("show-references", "author")
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("q", "Tesla")
                .appendQueryParameter("api-key", "test");
        String url = builder.build().toString();
        Log.i("URL is--------->", url);
        return url;
    }
        static URL createUrl() {
        String stringUrl = createStringUrl();
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Queryutils", "Error creating URL: ", e);
            return null;
        }
    }
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return "";
        }
    }
    static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Queryutils", "Error making HTTP request: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    static List<NewsItems> parseJson(String response) {
        ArrayList<NewsItems> listOfNewsItems = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject jsonResults = jsonResponse.getJSONObject(KEY_RESPONSE );
            JSONArray resultsArray = jsonResults.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject oneResult = resultsArray.getJSONObject(i);
                String webTitle = oneResult.getString(KEY_WEB_TITLE);
                String url = oneResult.getString(KEY_WEB_URL);
                String date = oneResult.getString(KEY_PUBLICATION_DATE);
                date = formatDate(date);
                String section = oneResult.getString(KEY_SECTION_NAME );
                JSONArray tagsArray = oneResult.getJSONArray(KEY_TAGS);
                String author = "";

                if (tagsArray.length() == 0) {
                    author = null;
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject firstObject = tagsArray.getJSONObject(j);
                        author += firstObject.getString(KEY_WEB_TITLE) + ". ";
                    }
                }
                listOfNewsItems.add(new NewsItems(webTitle, author, url, date, section));
            }
        } catch (JSONException e) {
            Log.e("Queryutils", "Error parsing JSON response", e);
        }
        return listOfNewsItems;
    }
}
