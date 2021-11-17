package com.lot.chatandtranslate;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImageUri;
    TextView username, job;

    public FriendsViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        profileImageUri = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
        job = itemView.findViewById(R.id.job);
    }
}
