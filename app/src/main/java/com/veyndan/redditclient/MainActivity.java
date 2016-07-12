package com.veyndan.redditclient;

import android.os.Bundle;

import butterknife.ButterKnife;
import rawjava.Reddit;
import rawjava.network.Credentials;
import rawjava.network.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String TAG = "veyndan_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        Reddit reddit = new Reddit(credentials);

        final PostsFragment postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

        reddit.subreddit("all", Sort.HOT, postsFragment.getNextPageTrigger(), Schedulers.io(), AndroidSchedulers.mainThread())
                .subscribe(post -> {
                    postsFragment.addPosts(post.data.children);
                });
    }
}
