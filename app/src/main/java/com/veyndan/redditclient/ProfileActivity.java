package com.veyndan.redditclient;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.airbnb.deeplinkdispatch.DeepLink;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rawjava.Reddit;
import rawjava.model.Thing;
import rawjava.model.Trophy;
import rawjava.network.Credentials;
import rawjava.network.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@DeepLink({
        "http://reddit.com/u/{username}",
        "http://reddit.com/user/{username}"
})
public class ProfileActivity extends BaseActivity {

    private static final String TAG = "veyndan_ProfileActivity";

    private static final int TAB_COUNT = 4;

    @BindView(R.id.profile_link_karma) TextView linkKarma;
    @BindView(R.id.profile_comment_karma) TextView commentKarma;
    @BindView(R.id.profile_trophies_recycler_view) RecyclerView trophiesRecyclerView;
    @BindView(R.id.profile_view_pager) ViewPager viewPager;
    @BindView(R.id.profile_tabs) TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        String username = getIntent().getStringExtra("username");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(username);

        final Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        final Reddit reddit = new Reddit(credentials);

        reddit.userAbout(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(thing -> {
                    linkKarma.setText(NumberFormat.getNumberInstance().format(thing.data.linkKarma));
                    commentKarma.setText(NumberFormat.getNumberInstance().format(thing.data.commentKarma));
                });

        final List<Thing<Trophy>> trophies = new ArrayList<>();

        TrophyAdapter trophyAdapter = new TrophyAdapter(trophies);
        trophiesRecyclerView.setAdapter(trophyAdapter);

        reddit.userTrophies(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(thing -> {
                    trophies.addAll(thing.data.trophies);
                    trophyAdapter.notifyDataSetChanged();
                });

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
