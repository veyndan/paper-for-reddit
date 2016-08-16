package com.veyndan.redditclient;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.model.Trophy;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.api.reddit.network.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@DeepLink({
        "http://reddit.com/u/{username}",
        "http://reddit.com/user/{username}"
})
public class ProfileActivity extends BaseActivity {

    private static final int TAB_COUNT = 4;

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    @BindView(R.id.profile_link_karma) TextView linkKarma;
    @BindView(R.id.profile_comment_karma) TextView commentKarma;
    @BindView(R.id.profile_trophies_recycler_view) RecyclerView trophiesRecyclerView;
    @BindView(R.id.profile_view_pager) ViewPager viewPager;
    @BindView(R.id.profile_tabs) TabLayout tabs;

    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    private final CustomTabsIntent customTabsIntent = builder.build();
    @Nullable private CustomTabsClient customTabsClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        final String username = getIntent().getStringExtra("username");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(username);

        final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
        final Reddit reddit = new Reddit.Builder(credentials).build();

        reddit.userAbout(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    linkKarma.setText(NumberFormat.getNumberInstance().format(response.body().data.linkKarma));
                    commentKarma.setText(NumberFormat.getNumberInstance().format(response.body().data.commentKarma));
                });

        final List<Thing<Trophy>> trophies = new ArrayList<>();

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(final ComponentName name, final CustomTabsClient client) {
                // customTabsClient is now valid.
                customTabsClient = client;
                customTabsClient.warmup(0);
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {
                // customTabsClient is no longer valid. This also invalidates sessions.
                customTabsClient = null;
            }
        });

        final TrophyAdapter trophyAdapter = new TrophyAdapter(this, trophies, customTabsClient, customTabsIntent);
        trophiesRecyclerView.setAdapter(trophyAdapter);

        reddit.userTrophies(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    trophies.addAll(response.body().data.trophies);
                    trophyAdapter.notifyDataSetChanged();
                });

        viewPager.setAdapter(new ProfileSectionAdapter(getSupportFragmentManager(), username));

        tabs.setupWithViewPager(viewPager);
    }

    private static class ProfileSectionAdapter extends FragmentStatePagerAdapter {

        private static final String titles[] = {"Overview", "Comments", "Submitted", "Gilded"};

        private final PostsFragment overviewFragment;
        private final PostsFragment commentsFragment;
        private final PostsFragment submittedFragment;
        private final PostsFragment gildedFragment;

        private final Reddit reddit;

        public ProfileSectionAdapter(final FragmentManager fm, final String username) {
            super(fm);
            final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
            reddit = new Reddit.Builder(credentials).build();

            overviewFragment = PostsFragment.newInstance();
            commentsFragment = PostsFragment.newInstance();
            submittedFragment = PostsFragment.newInstance();
            gildedFragment = PostsFragment.newInstance();

            reddit.user(username, User.OVERVIEW)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        overviewFragment.addPosts(response.body().data.children);
                    });

            reddit.user(username, User.COMMENTS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        commentsFragment.addPosts(response.body().data.children);
                    });

            reddit.user(username, User.SUBMITTED)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        submittedFragment.addPosts(response.body().data.children);
                    });

            reddit.user(username, User.GILDED)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        gildedFragment.addPosts(response.body().data.children);
                    });
        }

        @Override
        public Fragment getItem(final int position) {
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
        public CharSequence getPageTitle(final int position) {
            return titles[position];
        }
    }
}
