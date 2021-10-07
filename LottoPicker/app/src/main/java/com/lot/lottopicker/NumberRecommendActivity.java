package com.lot.lottopicker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class NumberRecommendActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    private int[] lotto_numbers;
    private int[] most_occurred_nums;

    private Button logoutBtn;
    private Button repickBtn;
    ImageView profileImage;
    private ImageView img_drw1;
    private ImageView img_drw2;
    private ImageView img_drw3;
    private ImageView img_drw4;
    private ImageView img_drw5;
    private ImageView img_drw6;
    private ArrayList<ImageView> imgViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        img_drw1 = findViewById(R.id.imageView1);
        img_drw2 = findViewById(R.id.imageView2);
        img_drw3 = findViewById(R.id.imageView3);
        img_drw4 = findViewById(R.id.imageView4);
        img_drw5 = findViewById(R.id.imageView5);
        img_drw6 = findViewById(R.id.imageView6);
        repickBtn = findViewById(R.id.repick_button);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    status -> {
                        if (status.isSuccess()) {
                            gotoMainActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        AsyncTaskClass asyncTaskClass = new AsyncTaskClass();
        try {
            HashMap hm = asyncTaskClass.execute().get();
            Set<Map.Entry<String, Integer>> set = hm.entrySet();
            List<Map.Entry<String, Integer>> list = new ArrayList<>(set);
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue())); //list.sublist(0, 5) 과출현번호 6개
            most_occurred_nums = new int[6];
            lotto_numbers = new int[6];
            Random rnd = new Random();

            for (int i = 0; i < 6; i++) {
                most_occurred_nums[i] = Integer.parseInt(list.get(i).getKey().replaceAll("[^0-9]", ""));
            }

            Arrays.sort(lotto_numbers);
            imgViewList = new ArrayList<>();
            imgViewList.add(img_drw1);
            imgViewList.add(img_drw2);
            imgViewList.add(img_drw3);
            imgViewList.add(img_drw4);
            imgViewList.add(img_drw5);
            imgViewList.add(img_drw6);

            repickBtn.setOnClickListener(v -> {
                for (int i = 0; i < lotto_numbers.length; i++) {
                    lotto_numbers[i] = getRandomWithExclusion(rnd, 1, 45, most_occurred_nums);

                    for (int j = 0; j < i; j++) {
                        if (lotto_numbers[i] == lotto_numbers[j]) {
                            i--;
                            break;
                        }
                    }
                }

                int[] icons = new int[lotto_numbers.length];

                for (int i = 0; i < lotto_numbers.length; i++) {
                    String tmpName = "ball_" + lotto_numbers[i];
                    icons[i] = getResources().getIdentifier(tmpName, "drawable", getPackageName());
                }

                for (int i = 0; i < imgViewList.size(); i++) {
                    imgViewList.get(i).setImageResource(icons[i]);
                }
            });

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(this::handleSignInResult);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            try {
                Glide.with(this).load(account.getPhotoUrl()).into(profileImage);
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "image not found", Toast.LENGTH_LONG).show();
            }

        } else {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}