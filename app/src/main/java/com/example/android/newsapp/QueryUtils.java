package com.example.android.newsapp;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lilla on 26/07/17.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    /*Fetch data*/
    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", ioException);
        }
        List<News> news = extractJSON(jsonResponse);
        return news;
    }

    /*Create URL*/
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException malformedUrlException) {
            Log.e(LOG_TAG, "Problem building the URL ", malformedUrlException);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", ioException);
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

    private static List<News> extractJSON (String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<News> newsArrayList = new ArrayList<>();

        try {
            JSONObject baseJSONObject = new JSONObject(newsJSON);

            JSONObject responseObject;
            if (baseJSONObject.has("response")){
                responseObject = baseJSONObject.getJSONObject("response");

                JSONArray resultsArray = responseObject.getJSONArray("results");
                if (responseObject.has("results")){
                    for (int r = 0; r < resultsArray.length(); r++) {
                        JSONObject currentNewsObject = resultsArray.getJSONObject(r);

                        String title;
                        if(currentNewsObject.has("webTitle")) {
                            title = currentNewsObject.getString("webTitle");
                        } else {
                            title = "Title N/A";
                        }

                        String sectionName;
                        if (currentNewsObject.has("sectionName")){
                            sectionName = currentNewsObject.getString("sectionName");
                        } else {
                            sectionName = "Section N/A";
                        }

                        String webUrl;
                        if (currentNewsObject.has("webUrl")) {
                            webUrl = currentNewsObject.getString("webUrl");
                        } else {
                            webUrl = "";
                        }

                        News news = new News(title, sectionName, webUrl);
                        newsArrayList.add(news);
                    }
                } else {
                    return Collections.emptyList();
                }

            } else {
                return Collections.emptyList();
            }

        } catch (JSONException jsonException) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", jsonException);
        }

        return newsArrayList;
    }

}
