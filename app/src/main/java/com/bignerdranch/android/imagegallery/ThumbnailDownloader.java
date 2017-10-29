package com.bignerdranch.android.imagegallery;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by yjoo9_000 on 2017-10-29.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloader(){
        super(TAG);
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "Image URL: "+url);
    }
}