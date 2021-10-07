package com.example.voicereaderapp.API;

import com.example.voicereaderapp.AsyncTaskClass;

import java.util.concurrent.ExecutionException;


public class RequestAPI {

    public String getUserInfo(String region, String summonerName) throws ExecutionException, InterruptedException {
        String str = "https://" + region + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;
        AsyncTaskClass asyncTaskClass = new AsyncTaskClass();
        String output = asyncTaskClass.execute(str).get();
        return output;
    }

    public String findRank(String region, String id) throws ExecutionException, InterruptedException {
        String str = "https://" + region + ".api.riotgames.com/lol/league/v4/entries/by-summoner/" + id;
        AsyncTaskClass asyncTaskClass = new AsyncTaskClass();
        String output = asyncTaskClass.execute(str).get();
        return output;
    }
}
