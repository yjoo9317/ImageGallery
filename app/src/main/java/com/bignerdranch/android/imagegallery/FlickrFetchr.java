package com.bignerdranch.android.imagegallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjoo9_000 on 2017-10-29.
 */

public class FlickrFetchr {
    private static final String TAG="FlickrFetchr";
    private static final String API_KEY = "f47858390fdfb6f486b2e5f1723bb947";

    private String getURLContents(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try{
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            Log.i(TAG, sb.toString());

        } finally{
            if(is != null)
                is.close();
            connection.disconnect();
        }
        return sb.toString();
    }
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+": with "+ urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[2048];
            while((bytesRead = in.read()) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            Log.d(TAG, "out size: "+out.size());
            return out.toByteArray();
        } finally{
            connection.disconnect();
        }
    }


    public String getUrlString(String urlSpec) throws IOException {
        return getURLContents(urlSpec);
    }

    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/").buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: "+jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch(IOException ioe){
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }
    private void parseItems(List<GalleryItem> items, JSONObject json)throws IOException, JSONException{
        JSONObject photosJsonObject = json.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photoJsonArray.length(); i++){
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            Log.i(TAG, "title: "+item.getCaption());
            if(!photoJsonObject.has("url_s"))
                continue;

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
