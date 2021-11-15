package com.lot.chatandtranslate;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username, job;
    public FindFriendViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
        job = itemView.findViewById(R.id.job);
    }
}
