package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef, requestRef, friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUri, username, city, job;

    Button btnPerform, btnDecline;
    CircleImageView profileImage;
    TextView Username, address;
    String CurrentState = "NONE";
    String userID;

    String myProfileImageUri, myProfileUsername, myProfileCity, myProfileJob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        userID = getIntent().getStringExtra("userKey");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        profileImage = findViewById(R.id.view_profileImage);
        Username = findViewById(R.id.username);
        address = findViewById(R.id.address);
        btnPerform = findViewById(R.id.btnSendRequest);
        btnDecline = findViewById(R.id.btnDeclineRequest);
        LoadUser();
        LoadMyProfile();
        btnPerform.setOnClickListener(v -> PerformAction(userID));
        CheckUserExistance(userID);
        btnDecline.setOnClickListener(v -> {
            Unfriend(userID);
        });
    }

    private void Unfriend(String userID) {
        if(CurrentState.equals("friend")) {
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(ViewFriendActivity.this, "친구 취소", Toast.LENGTH_SHORT).show();
                                    CurrentState = "NONE";
                                    btnPerform.setText("친구 요청");
                                    btnDecline.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if(CurrentState.equals("RECEIVED_REQUEST")) {
            HashMap map = new HashMap();
            map.put("status", "decline");
            requestRef.child(userID).child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "친구 취소", Toast.LENGTH_SHORT).show();
                        CurrentState = "REQUEST_DECLINE";
                        btnPerform.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void CheckUserExistance(String userID) {

        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    CurrentState = "friend";
                    btnPerform.setText("메세지 보내기");
                    btnDecline.setText("친구 취소");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    CurrentState = "friend";
                    btnPerform.setText("메세지 보내기");
                    btnDecline.setText("친구 취소");
                    btnDecline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("status").getValue().toString().equals("pending")) {
                        CurrentState = "SENT";
                        btnPerform.setText("친구 요청 취소");
                        btnDecline.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline")) {
                        CurrentState = "DECLINE";
                        btnPerform.setText("친구 요청 취소");
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
            requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(snapshot.child("status").getValue().toString().equals("pending")) {
                            CurrentState = "RECEIVED_REQUEST";
                            btnPerform.setText("친구 수락");
                            btnDecline.setText("친구 취소");
                            btnDecline.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        if(CurrentState.equals("NONE")) {
            CurrentState = "NONE";
            btnPerform.setText("친구 요청");
            btnDecline.setVisibility(View.GONE);
        }
    }

    private void PerformAction(String userID) {
        if(CurrentState.equals("NONE")) {
            HashMap map = new HashMap();
            map.put("status", "pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(map).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(ViewFriendActivity.this, "친구요청을 보냈습니다", Toast.LENGTH_SHORT).show();
                    btnDecline.setVisibility(View.GONE);
                    CurrentState = "SENT";
                    btnPerform.setText("친구 요청 취소");
                } else {
                    Toast.makeText(ViewFriendActivity.this, "친구요청 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(CurrentState.equals("SENT") || CurrentState.equals("DECLINE")) {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "친구 요청 취소", Toast.LENGTH_SHORT).show();
                        CurrentState = "NONE";
                        btnPerform.setText("친구 요청");
                        btnDecline.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //userID = 보낸사람 mUser.getUid = 받는사람
        if(CurrentState.equals("RECEIVED_REQUEST")) {
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        HashMap map = new HashMap();
                        map.put("status", "friend");
                        map.put("username", username);
                        map.put("profileImageUri", profileImageUri);
                        map.put("job", job);

                        HashMap map1 = new HashMap();
                        map1.put("status", "friend");
                        map1.put("username", myProfileUsername);
                        map1.put("profileImageUri", myProfileImageUri);
                        map1.put("job", myProfileJob);


                        friendRef.child(mUser.getUid()).child(userID).updateChildren(map).addOnCompleteListener(task1 -> {

                            if(task1.isSuccessful()) {
                                friendRef.child(userID).child(mUser.getUid()).updateChildren(map1).addOnCompleteListener(task11 -> {
                                    Toast.makeText(ViewFriendActivity.this, "친구 추가 완료", Toast.LENGTH_SHORT).show();
                                    CurrentState = "friend";
                                    btnPerform.setText("메세지 보내기");
                                    btnDecline.setText("친구 취소");
                                    btnDecline.setVisibility(View.VISIBLE);
                                });
                            }
                        });
                    }
                }
            });
        }
        if(CurrentState.equals("friend")) {
            Intent intent = new Intent(ViewFriendActivity.this, ChatActivity.class);
            intent.putExtra("OtherUserID", userID);
            startActivity(intent);
        }
    }

    private void LoadUser() {
        mUserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    profileImageUri = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    city = snapshot.child("city").getValue().toString();
                    job = snapshot.child("job").getValue().toString();

                    Picasso.get().load(profileImageUri).into(profileImage);
                    Username.setText(username);
                    address.setText(city);
                } else {
                    Toast.makeText(ViewFriendActivity.this, "데이터 찾기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "요청 취소", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    myProfileImageUri = snapshot.child("profileImage").getValue().toString();
                    myProfileUsername = snapshot.child("username").getValue().toString();
                    myProfileCity = snapshot.child("city").getValue().toString();
                    myProfileJob = snapshot.child("job").getValue().toString();

                } else {
                    Toast.makeText(ViewFriendActivity.this, "데이터 찾기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "요청 취소", Toast.LENGTH_SHORT).show();
            }
        });
    }
}