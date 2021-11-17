package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lot.chatandtranslate.Utils.Friends;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ChatUsersActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        LoadFriends("");
    }

    private void LoadFriends(String s) {
        Query query = mRef.child(mUser.getUid()).orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull FriendsViewHolder holder, int position, @NonNull @NotNull Friends model) {
                Picasso.get().load(model.getProfileImageUri()).into(holder.profileImageUri);
                holder.username.setText(model.getUsername());
                holder.job.setText(model.getJob());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatUsersActivity.this, ChatActivity.class);
                        intent.putExtra("OtherUserID", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend,parent,false);
                return new FriendsViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}