package com.lot.chatandtranslate;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageView;
    TextView username, comment;
    public CommentViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        circleImageView = itemView.findViewById(R.id.profileImage_comment);
        username = itemView.findViewById(R.id.username_comment);
        comment = itemView.findViewById(R.id.comment_text);
    }
}
