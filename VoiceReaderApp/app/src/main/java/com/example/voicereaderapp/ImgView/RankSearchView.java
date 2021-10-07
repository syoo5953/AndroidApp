package com.example.voicereaderapp.ImgView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.captaindroid.tvg.Tvg;
import com.example.voicereaderapp.API.RequestAPI;
import com.example.voicereaderapp.AsyncTaskClass;
import com.example.voicereaderapp.LoginActivity;
import com.example.voicereaderapp.MainActivity;
import com.example.voicereaderapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;

public class RankSearchView extends AppCompatActivity {
    TextView txt_rank, rank_id;
    Button btn_rank;
    Spinner dropdown;
    RequestAPI requestAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_search_view);
        requestAPI = new RequestAPI();
        txt_rank = findViewById(R.id.txt_rank);
        rank_id = findViewById(R.id.rank_id);
        btn_rank = findViewById(R.id.btn_rank);
        dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"BR1", "EUN1", "EUW1", "JP1", "KR", "LA1", "LA2", "NA1", "OC1", "RU", "TR1"};
        ArrayAdapter<String> adapterSpinnerSource = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapterSpinnerSource);
        Tvg.change(txt_rank, Color.parseColor("#800CDD"),  Color.parseColor("#3BA3F2"));

        btn_rank.setOnClickListener(v -> {
            String rankID = rank_id.getText().toString();
            String region = dropdown.getSelectedItem().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //your code or your request that you want to run on uiThread
                            RequestAPI requestAPI = new RequestAPI();
                            String userInfo;
                            String findTier;
                            try {
                                userInfo = requestAPI.getUserInfo(region, rankID);
                                JSONObject jsonObject = new JSONObject(userInfo);
                                String id = jsonObject.getString("id");
                                String accountId = jsonObject.getString("accountId");
                                String puuid = jsonObject.getString("puuid");

                                findTier = requestAPI.findRank(region, id);
                                String tier = null;
                                JSONArray jsonArray = new JSONArray(findTier);
                                for (int count = 0; count < jsonArray.length(); count++) {
                                    JSONObject obj = jsonArray.getJSONObject(count);
                                    tier = obj.getString("tier");
                                    Intent intent = new Intent(RankSearchView.this, RankImgView.class);
                                    intent.putExtra("tier", tier);
                                    startActivity(intent);
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }).start();
        });
    }
}