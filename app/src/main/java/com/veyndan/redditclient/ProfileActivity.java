package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {

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

        tabs.addTab(tabs.newTab().setText("Overview"));
        tabs.addTab(tabs.newTab().setText("Comments"));
        tabs.addTab(tabs.newTab().setText("Submitted"));
        tabs.addTab(tabs.newTab().setText("Gilded"));

        tabs.setupWithViewPager(viewPager);
    }
}
