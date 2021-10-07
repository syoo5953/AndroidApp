package com.lot.lottopicker;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class AsyncTaskClass extends AsyncTask<JSONObject, Void, HashMap> {


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected HashMap doInBackground(JSONObject... strings) {
        HashMap<String, Integer> hm = new HashMap<>();
        try {
            String json = readUrl();
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 800; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String[] str = jsonObject.getString("numbers").split(",");
                String bonus_str = jsonObject.getString("bonus_no");

                for(String key : str) {
                    hm.put(key, hm.getOrDefault(key, 0) + 1);
                }
                hm.put(bonus_str, hm.getOrDefault(bonus_str, 0) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hm;
    }

    private static String readUrl() throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL("https://smok95.github.io/lotto/results/all.json");
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
