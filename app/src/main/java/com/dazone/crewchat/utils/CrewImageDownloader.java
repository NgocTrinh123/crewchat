package com.dazone.crewchat.utils;

import android.content.Context;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Admin on 7/21/2016.
 */
public class CrewImageDownloader extends BaseImageDownloader {
    public CrewImageDownloader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        URL url = new URL(imageUri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.connect();
        while (conn.getResponseCode() == 302) { // >=300 && < 400
            String redirectUrl = conn.getHeaderField("Location");
            conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();
        }
        return new FlushedInputStream(conn.getInputStream());
    }
}
