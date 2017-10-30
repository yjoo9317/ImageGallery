package com.bignerdranch.android.imagegallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by yjoo9_000 on 2017-10-29.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    public static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    public ThumbnailDownloader(){
        super(TAG);
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Image URL: "+url);
        if(url == null){
            mRequestMap.remove(target);
        } else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
          @Override
            public void handleMessage(Message msg){
              if(msg.what == MESSAGE_DOWNLOAD){
                  T target = (T) msg.obj;
                  handleRequest(target);
              }
          }
        };
    }

    private void handleRequest(final T target){
        try{
            String url = mRequestMap.get(target);
            if(url == null) return;
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        }catch(IOException e){
            Log.e(TAG, "Failed to download Image", e);
        }
    }
}
