package com.veyndan.redditclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rawjava.Reddit;
import rawjava.network.Credentials;
import rawjava.network.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        Objects.requireNonNull(ab, "An ActionBar should be attached to this activity");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(username);

        viewPager.setAdapter(new ProfileSectionAdapter(getSupportFragmentManager(), this, username));

        tabs.setupWithViewPager(viewPager);
    }

    private static class ProfileSectionAdapter extends FragmentStatePagerAdapter {

        private static final String titles[] = {"Overview", "Comments", "Submitted", "Gilded"};

        private final PostsFragment overviewFragment;
        private final PostsFragment commentsFragment;
        private final PostsFragment submittedFragment;
        private final PostsFragment gildedFragment;

        private final Reddit reddit;

        public ProfileSectionAdapter(FragmentManager fm, Context context, String username) {
            super(fm);
            Credentials credentials = Credentials.create(context.getResources().openRawResource(R.raw.credentials));
            reddit = new Reddit(credentials);

            overviewFragment = PostsFragment.newInstance();
            commentsFragment = PostsFragment.newInstance();
            submittedFragment = PostsFragment.newInstance();
            gildedFragment = PostsFragment.newInstance();

            reddit.user(username, User.OVERVIEW)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(thing -> overviewFragment.addPosts(thing.data.children));

            reddit.user(username, User.COMMENTS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(thing -> commentsFragment.addPosts(thing.data.children));

            reddit.user(username, User.SUBMITTED)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(thing -> submittedFragment.addPosts(thing.data.children));

            reddit.user(username, User.GILDED)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(thing -> gildedFragment.addPosts(thing.data.children));
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return overviewFragment;
                case 1:
                    return commentsFragment;
                case 2:
                    return submittedFragment;
                case 3:
                    return gildedFragment;
                default:
                    throw new IllegalStateException("Too many fragments");
            }
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
