package com.hustleandswag.earlytwo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.hustleandswag.earlytwo.R;

public class MyTopPostsFragment extends PostListFragment {

    public MyTopPostsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]
        // Top posts by number of likes
        Query myTopPostsQuery = databaseReference.child("posts")
                .orderByChild("likeCount");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }
}
