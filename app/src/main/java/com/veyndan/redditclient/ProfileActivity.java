package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {

    private static final int TAB_COUNT = 4;

    @BindView(R.id.profile_view_pager) ViewPager viewPager;
    @BindView(R.id.profile_tabs) TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        String username;
        final Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        } else {
            throw new IllegalStateException("Activity started by unknown caller");
        }

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(username);
        }

        viewPager.setAdapter(new ProfileSectionAdapter(getSupportFragmentManager()));

        tabs.setupWithViewPager(viewPager);
    }

    private static class ProfileSectionAdapter extends FragmentStatePagerAdapter {

        private static final String titles[] = {"Overview", "Comments", "Submitted", "Gilded"};

        public ProfileSectionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ProfileSectionFragment.newInstance();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
