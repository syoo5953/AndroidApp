package com.lot.chatandtranslate;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    ImageView postImage, likeImage, commentImage, commentSend;
    TextView username, timeAgo, postDesc, likeCnt, commentCnt;
    EditText inputComments;
    public static RecyclerView recyclerView;

    public MyViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileImgPost);
        postImage = itemView.findViewById(R.id.postImage);
        username = itemView.findViewById(R.id.profileUsernamePost);
        timeAgo = itemView.findViewById(R.id.timeAgo);
        postDesc = itemView.findViewById(R.id.postDesc);
        likeImage = itemView.findViewById(R.id.like);
        commentImage = itemView.findViewById(R.id.comment);
        likeCnt = itemView.findViewById(R.id.likeCounter);
        commentCnt = itemView.findViewById(R.id.commentCnt);
        inputComments = itemView.findViewById(R.id.inputComment);
        commentSend = itemView.findViewById(R.id.send_comment);
        recyclerView = itemView.findViewById(R.id.recyclerViewComments);
    }

    public void countLikes(String postKey, String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalLikes = (int)snapshot.getChildrenCount();
                    likeCnt.setText(totalLikes+"");
                } else {
                    likeCnt.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        likeRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()) {
                    likeImage.setColorFilter(Color.GREEN);
                } else {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void countComment(String postKey, String uid, DatabaseReference commentRef) {
        commentRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalComments = (int)snapshot.getChildrenCount();
                    commentCnt.setText(totalComments+"");
                } else {
                    commentCnt.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
