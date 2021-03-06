package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lot.chatandtranslate.Utils.Comment;
import com.lot.chatandtranslate.Utils.Posts;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_CODE = 101;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef, mPostRef, likeRef, commentRef;

    String profileURIImgV, usernameV;
    CircleImageView profileImgHeader;
    TextView usernameHeader;
    ImageView addImgPost, sendImgPost;
    EditText inputPostDescription;
    Uri imgUri;
    ProgressDialog mLoadingBar;
    StorageReference sRef;
    FirebaseRecyclerAdapter<Posts, MyViewHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Comment> commentOptions;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> commnetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_bar);
        mLoadingBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("???????????? App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        addImgPost = findViewById(R.id.add_img_post);
        sendImgPost = findViewById(R.id.send_post_img);
        inputPostDescription = findViewById(R.id.inputAddPost);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        sRef = FirebaseStorage.getInstance().getReference().child("PostImages");

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImgHeader = view.findViewById(R.id.profileImage_header);
        usernameHeader = view.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());

        sendImgPost.setOnClickListener(v -> AddPost());
        addImgPost.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE);
        });

        LoadPost();
    }

    private void LoadPost() {
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(mPostRef,Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position, @NonNull @NotNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = format.parse(model.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timeAgo = formatTimeString(date);
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());

                Picasso.get().load(model.getPostImageUri()).into(holder.postImage);
                Picasso.get().load(model.getUserProfileImageUri()).into(holder.profileImage); // ????????? ???????????? ?????????
                holder.countLikes(postKey,mUser.getUid(),likeRef);
                holder.countComment(postKey,mUser.getUid(),commentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    likeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                } else {
                                    likeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    holder.likeImage.setColorFilter(Color.GREEN);
                                    notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.commentSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = holder.inputComments.getText().toString();
                        if(comment.isEmpty()) {
                            Toast.makeText(MainActivity.this, "???????????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            AddComment(holder,postKey,commentRef,mUser.getUid(),comment);
                        }
                    }
                });
                LoadComment(postKey);
                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
                        intent.putExtra("uri", model.getPostImageUri());
                        startActivity(intent);
                    }
                });
            }

            @NotNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void LoadComment(String postKey) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        MyViewHolder.recyclerView.setLayoutManager(linearLayoutManager);
        commentOptions = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentRef.child(postKey),Comment.class).build();
        commnetAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position, @NonNull @NotNull Comment model) {
                Picasso.get().load(model.getProfileImageUri()).into(holder.circleImageView);
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComment());
            }

            @NotNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
                return new CommentViewHolder(view);
            }
        };
        commnetAdapter.startListening();
        MyViewHolder.recyclerView.setAdapter(commnetAdapter);
    }

    private void AddComment(MyViewHolder holder, String postKey, DatabaseReference commentRef, String uid, String comment) {
        HashMap map = new HashMap();
        map.put("username", usernameV);
        map.put("profileImageUri", profileURIImgV);
        map.put("comment", comment);

        commentRef.child(postKey).child(uid).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "?????? ????????????", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    holder.inputComments.setText(null);
                } else {
                    Toast.makeText(MainActivity.this, "?????? ????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(Date tempDate) {

        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;

        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "?????? ???";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "??? ???";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "?????? ???";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "??? ???";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "??? ???";
        } else {
            msg = (diffTime) + "??? ???";
        }
        return msg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            addImgPost.setImageURI(imgUri);
        }
    }

    private void AddPost() {
        String postDescription = inputPostDescription.getText().toString();
        if(postDescription.isEmpty() || postDescription.length() < 3) {
            inputPostDescription.setError("?????? ??????????????????.(3?????? ??????)");
        } else if(imgUri == null) {
            Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
        } else {
            mLoadingBar.setTitle("????????????...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = formatter.format(date);

            sRef.child(mUser.getUid()+strDate).putFile(imgUri).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    sRef.child(mUser.getUid()+strDate).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap map = new HashMap();
                        map.put("date", strDate);
                        map.put("postImageUri", uri.toString());
                        map.put("postDesc", postDescription);
                        map.put("userProfileImageUri", profileURIImgV);
                        map.put("username", usernameV);
                        mPostRef.child(mUser.getUid()+strDate).updateChildren(map).addOnSuccessListener(o -> {
                            if(task.isSuccessful()) {
                                mLoadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                                addImgPost.setImageResource(R.drawable.ic_add_post_image);
                                inputPostDescription.setText("");
                            } else {
                                mLoadingBar.dismiss();
                                Toast.makeText(MainActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    mLoadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profileImgPost:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.friend:
                startActivity(new Intent(MainActivity.this, FriendActivity.class));
                break;
            case R.id.find_friend:
                startActivity(new Intent(MainActivity.this, FindFriendActivity.class));
                break;
            case R.id.chat:
                startActivity(new Intent(MainActivity.this, ChatUsersActivity.class));
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mUser==null) {
            SendUserToLoginInActivity();
        } else {
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        profileURIImgV = snapshot.child("profileImage").getValue().toString();
                        usernameV = snapshot.child("username").getValue().toString();
                        Picasso.get().load(profileURIImgV).into(profileImgHeader);
                        usernameHeader.setText(usernameV);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginInActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}