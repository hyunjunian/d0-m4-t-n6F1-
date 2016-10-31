package com.hustleandswag.earlytwo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hustleandswag.earlytwo.R;

public class ProfileFragment extends Fragment {

    private DatabaseReference mDatabase;

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();


        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tv_user_name = (TextView) rootView.findViewById(R.id.user_name);
        tv_user_name.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        return rootView;
    }
}
