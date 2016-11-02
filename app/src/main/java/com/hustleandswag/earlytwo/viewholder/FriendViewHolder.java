package com.hustleandswag.earlytwo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustleandswag.earlytwo.R;
import com.hustleandswag.earlytwo.models.User;

/**
 * Created by hyunjunjeon on 02/11/2016.
 */

public class FriendViewHolder extends RecyclerView.ViewHolder {

    public ImageView faceView;
    public TextView nameView;
    public Button addButton;

    public FriendViewHolder(View itemView) {
        super(itemView);

        faceView = (ImageView) itemView.findViewById(R.id.friend_image);
        nameView = (TextView) itemView.findViewById(R.id.friend_name);
        addButton = (Button) itemView.findViewById(R.id.button);
    }

    public void bindToFirend(User user, View.OnClickListener addClickListener) {
        //faceView.setImageURI();
        nameView.setText(user.username);
        addButton.setOnClickListener(addClickListener);
    }
}
