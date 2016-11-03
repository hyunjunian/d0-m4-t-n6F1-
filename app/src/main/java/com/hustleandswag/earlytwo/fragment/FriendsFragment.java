package com.hustleandswag.earlytwo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.hustleandswag.earlytwo.R;
import com.hustleandswag.earlytwo.models.User;
import com.hustleandswag.earlytwo.viewholder.FriendViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<User, FriendViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]


        mRecycler = (RecyclerView) rootView.findViewById(R.id.friends_list);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query usersQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<User, FriendViewHolder>(User.class, R.layout.item_friend,
                FriendViewHolder.class, usersQuery) {
            @Override
            protected void populateViewHolder(final FriendViewHolder viewHolder, final User model, final int position) {
                final DatabaseReference userRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = userRef.getKey();
                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });*/

                // Determine if the current user has liked this post and set UI accordingly
                if (model.friends.containsKey(getUid())) {
                    viewHolder.addButton.setText("bye bye~");
                } else {
                    viewHolder.addButton.setText("add");
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToFirend(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View addView) {
                        // Run two transactions
                        onAddClicked(userRef,mDatabase.child("users").child(getUid()).getRef());
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    private void onAddClicked(final DatabaseReference userRef, DatabaseReference meRef) {
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.friends.containsKey(getUid())) {
                    // Unstar the post and remove self from likes
                    u.friendCount = u.friendCount - 1;
                    u.friends.remove(getUid());
                } else {
                    // Star the post and add self to likes
                    u.friendCount = u.friendCount + 1;
                    u.friends.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        meRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.friends.containsKey(userRef.getKey())) {
                    // Unstar the post and remove self from likes
                    u.friendCount = u.friendCount - 1;
                    u.friends.remove(userRef.getKey());
                } else {
                    // Star the post and add self to likes
                    u.friendCount = u.friendCount + 1;
                    u.friends.put(userRef.getKey(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    private Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("users")
                .limitToFirst(100);
        // [END recent_posts_query]

        return recentPostsQuery;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
