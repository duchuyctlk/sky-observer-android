package com.huynd.skyobserver.utils;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by HuyND on 8/13/2017.
 */

public class FileUtils {
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromAssets(AssetManager manager, String filePath) throws IOException {
        InputStream fin = manager.open(filePath);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }
}
