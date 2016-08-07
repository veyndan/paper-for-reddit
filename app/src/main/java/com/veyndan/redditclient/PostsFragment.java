package com.veyndan.redditclient;

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

import java.util.ArrayList;
import java.util.List;

import rawjava.Reddit;
import rawjava.model.RedditObject;
import rawjava.network.Credentials;
import rx.Observable;

public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;

    private final List<RedditObject> posts = new ArrayList<>();

    private PostAdapter postAdapter;

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

        final Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        final Reddit reddit = new Reddit.Builder(credentials).build();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
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
