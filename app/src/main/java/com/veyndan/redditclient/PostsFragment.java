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
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.post.PostMvpView;
import com.veyndan.redditclient.post.PostPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import rx.Observable;

public class PostsFragment extends Fragment implements PostMvpView {

    private final PostPresenter postPresenter = new PostPresenter();

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
    public void onAttach(final Context context) {
        super.onAttach(context);
        postPresenter.attachView(this);

        final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID_RAWJAVA, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
        reddit = new Reddit.Builder(credentials).build();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFilter(final SubredditFilter filter) {
        clearPosts();

        postPresenter.loadPosts(filter);
    }

    public void addPosts(final List<RedditObject> posts) {
        this.posts.addAll(posts);
        if (postAdapter != null) {
            postAdapter.notifyDataSetChanged();
        }
        loadingPosts = false;
    }

    public void clearPosts() {
        final int postsSize = this.posts.size();
        this.posts.clear();
        if (postAdapter != null) {
            postAdapter.notifyItemRangeRemoved(0, postsSize);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
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

    @Override
    public void onDestroy() {
        postPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showPosts(final List<RedditObject> posts) {
        final int positionStart = this.posts.size() + 1;
        this.posts.addAll(posts);
        postAdapter.notifyItemRangeInserted(positionStart, posts.size());
        loadingPosts = false;
    }

    @Override
    public Observable<Boolean> getNextPageTrigger() {
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
