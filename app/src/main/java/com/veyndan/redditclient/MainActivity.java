package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rawjava.Reddit;
import rawjava.model.Link;
import rawjava.model.Listing;
import rawjava.model.Thing;
import rawjava.network.Credentials;
import rawjava.network.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String TAG = "veyndan_MainActivity";

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        Reddit reddit = new Reddit(credentials);

        final List<Thing<Link>> posts = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final PostAdapter postAdapter = new PostAdapter(posts, reddit, metrics.widthPixels);
        recyclerView.setAdapter(postAdapter);
        reddit.subreddit("all", Sort.HOT, getNextPageTrigger(), Schedulers.io(), AndroidSchedulers.mainThread())
                .subscribe(post -> {
                    posts.addAll(post.data.children);
                    postAdapter.notifyDataSetChanged();
                    loadingPosts = false;
                });
    }

    Observable<Thing<Listing<Thing<Link>>>> getNextPageTrigger() {
        return RxRecyclerView.scrollEvents(recyclerView)
                .filter(scrollEvent -> scrollEvent.dy() > 0) //check for scroll down
                .map(scrollEvent -> {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    return scrollEvent;
                })
                .filter(scrollEvent -> !loadingPosts && visibleItemCount + pastVisiblesItems >= totalItemCount)
                .map(scrollEvent -> {
                    loadingPosts = true;
                    return null;
                });
    }
}
