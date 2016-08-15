package com.veyndan.redditclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.veyndan.redditclient.api.Reddit;
import com.veyndan.redditclient.api.model.RedditObject;
import com.veyndan.redditclient.api.network.Credentials;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostsFragment extends Fragment {

    @BindDimen(R.dimen.card_view_margin) int cardViewMargin;

    private RecyclerView recyclerView;

    private final List<RedditObject> posts = new ArrayList<>();

    private PostAdapter postAdapter;

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private Reddit reddit;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        final Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        reddit = new Reddit.Builder(credentials).build();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFilter(final SubredditFilter filter) {
        clearPosts();

        reddit.subreddit(filter.getSubreddit(), filter.getSort(), filter.getQuery(), getNextPageTrigger(), Schedulers.io(), AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    addPosts(response.body().data.children);
                });
    }

    public void addPosts(List<RedditObject> posts) {
        this.posts.addAll(posts);
        if (postAdapter != null) {
            postAdapter.notifyDataSetChanged();
        }
        loadingPosts = false;
    }

    public void clearPosts() {
        int postsSize = this.posts.size();
        this.posts.clear();
        if (postAdapter != null) {
            postAdapter.notifyItemRangeRemoved(0, postsSize);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(cardViewMargin));
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        postAdapter = new PostAdapter(getActivity(), posts, reddit, metrics.widthPixels);
        recyclerView.setAdapter(postAdapter);

        return recyclerView;
    }

    Observable<Boolean> getNextPageTrigger() {
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
                    return true;
                });
    }
}
