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

import java.util.ArrayList;
import java.util.List;

import rawjava.Reddit;
import rawjava.model.Link;
import rawjava.model.Thing;
import rawjava.network.Credentials;

public class PostsFragment extends Fragment {

    private final List<Thing<Link>> posts = new ArrayList<>();

    private PostAdapter postAdapter;

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

    public void addPosts(List<Thing<Link>> posts) {
        this.posts.addAll(posts);
        if (postAdapter != null) {
            postAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);

        final Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        final Reddit reddit = new Reddit(credentials);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        final DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        postAdapter = new PostAdapter(posts, reddit, metrics.widthPixels);
        recyclerView.setAdapter(postAdapter);

        return recyclerView;
    }
}
