package com.example.s1636431.coinz;


import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;


public class CommunityActivity extends AppCompatActivity {

    private static final String TAG = "CommunityActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        Log.d(TAG, "onCreate: Starting.");

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfilePageFragment(), "Profile Page");
        adapter.addFragment(new LeaderBoardFragment(), "Leaderboard");
        adapter.addFragment(new FriendsListFragment(), "Friends List");
        viewPager.setAdapter(adapter);
    }




}
