package com.hustleandswag.earlytwo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.hustleandswag.earlytwo.fragment.EmptyFragment;
import com.hustleandswag.earlytwo.fragment.FriendsFragment;
import com.hustleandswag.earlytwo.fragment.MyPostsFragment;
import com.hustleandswag.earlytwo.fragment.MyTopPostsFragment;
import com.hustleandswag.earlytwo.fragment.ProfileFragment;
import com.hustleandswag.earlytwo.fragment.RecentPostsFragment;

public class  MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private BackKeyClose backKeycloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentPostsFragment(),
                    new MyPostsFragment(),
                    new MyTopPostsFragment(),
            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });

        // bottomNav
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_cards:
                                EmptyFragment newEmptyFragment = new EmptyFragment();
                                Bundle args0 = new Bundle();
                                //args.putInt(FriendsFragment.ARG_POSITION, position);
                                newEmptyFragment.setArguments(args0);

                                FragmentTransaction transaction0 = getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack so the user can navigate back
                                transaction0.replace(R.id.fragment_container, newEmptyFragment);
                                transaction0.addToBackStack(null);

                                // Commit the transaction
                                transaction0.commit();
                                break;
                            case R.id.action_me:
                                ProfileFragment newFragment = new ProfileFragment();
                                Bundle args = new Bundle();
                                //args.putInt(FriendsFragment.ARG_POSITION, position);
                                newFragment.setArguments(args);

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack so the user can navigate back
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();
                                break;
                            case R.id.action_friends:
                                FriendsFragment newFragment2 = new FriendsFragment();
                                Bundle args2 = new Bundle();
                                //args.putInt(FriendsFragment.ARG_POSITION, position);
                                newFragment2.setArguments(args2);

                                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack so the user can navigate back
                                transaction2.replace(R.id.fragment_container, newFragment2);
                                transaction2.addToBackStack(null);

                                // Commit the transaction
                                transaction2.commit();
                                break;
                        }
                        return false;
                    }
                });

        // backKey

        backKeycloseHandler = new BackKeyClose(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backKeycloseHandler.onBackPressed();
    }

}
