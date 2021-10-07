package com.example.voicereaderapp.ImgView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.voicereaderapp.R;

public class RankImgView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_img_view);
        Intent intent = getIntent();
        String tier = intent.getStringExtra("tier");
        ImageView imageView = findViewById(R.id.rank_img);
        if(tier.equalsIgnoreCase("iron")) {
            imageView.setImageResource(R.drawable.emblem_iron);
        } else if(tier.equalsIgnoreCase("bronze")) {
            imageView.setImageResource(R.drawable.emblem_bronze);
        } else if(tier.equalsIgnoreCase("silver")) {
            imageView.setImageResource(R.drawable.emblem_silver);
        } else if(tier.equalsIgnoreCase("gold")) {
            imageView.setImageResource(R.drawable.emblem_gold);
        }else if(tier.equalsIgnoreCase("platinum")) {
            imageView.setImageResource(R.drawable.emblem_platinum);
        }else if(tier.equalsIgnoreCase("diamond")) {
            imageView.setImageResource(R.drawable.emblem_diamond);
        }else if(tier.equalsIgnoreCase("master")) {
            imageView.setImageResource(R.drawable.emblem_master);
        }else if(tier.equalsIgnoreCase("grandmaster")) {
            imageView.setImageResource(R.drawable.emblem_grandmaster);
        }else if(tier.equalsIgnoreCase("challenger")) {
            imageView.setImageResource(R.drawable.emblem_challenger);
        }
    }
}