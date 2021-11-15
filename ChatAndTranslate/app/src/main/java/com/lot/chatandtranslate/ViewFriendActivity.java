package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    String profileImageUri, username, city;

    Button btnPerform, btnDecline;
    CircleImageView profileImage;
    TextView Username, address;
    String CurrentState = "NONE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        String userID = getIntent().getStringExtra("userKey");
        Toast.makeText(this, "유저아이디= "+userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
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

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(userID);
            }
        });
    }

    private void PerformAction(String userID) {
        if(CurrentState.equals("NONE")) {
            HashMap map = new HashMap();
            map.put("status", "pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ViewFriendActivity.this, "친구요청을 보냈습니다", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        CurrentState = "SENT";
                        btnPerform.setText("친구 요청 취소");
                    } else {
                        Toast.makeText(ViewFriendActivity.this, "친구요청 실패", Toast.LENGTH_SHORT).show();
                    }
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
        if(CurrentState.equals("RECEIVED")) {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        HashMap map = new HashMap();
                        map.put("status", "friend");
                        map.put("username", username);
                        map.put("profileImageUri", profileImageUri);
                        friendRef.child(mUser.getUid()).child(userID).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task task) {
                                if(task.isSuccessful()) {
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this, "친구 추가 완료", Toast.LENGTH_SHORT).show();
                                            CurrentState = "FRIEND";
                                            btnPerform.setText("메세지 보내기");
                                            btnDecline.setText("친구 취소");
                                            btnDecline.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        if(CurrentState.equals("FRIEND")) {

        }
    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    profileImageUri = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    city = snapshot.child("city").getValue().toString();

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
}