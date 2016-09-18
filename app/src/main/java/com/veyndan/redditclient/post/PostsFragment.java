package com.veyndan.redditclient.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.UserFilter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.MarginItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class PostsFragment extends Fragment implements PostMvpView {

    private static final String ARG_USER_FILTER = "user_filter";

    private final PostPresenter postPresenter = new PostPresenter();

    private RecyclerView recyclerView;

    private final List<Tree.Node<Post>> nodes = new ArrayList<>();

    private PostAdapter postAdapter;

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;

    private Reddit reddit;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(final UserFilter userFilter) {
        final PostsFragment fragment = new PostsFragment();

        final Bundle args = new Bundle();
        args.putParcelable(ARG_USER_FILTER, userFilter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        postPresenter.attachView(this);

        reddit = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFilter(final PostsFilter filter) {
        clearNodes();
        nodes.add(new Tree.Node<>(null, true));
        postPresenter.loadPosts(filter, getNextPageTrigger());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        postAdapter = new PostAdapter(getActivity(), nodes, reddit);
        recyclerView.setAdapter(postAdapter);

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        final Bundle args = getArguments();
        if (args != null) {
            setFilter(args.getParcelable(ARG_USER_FILTER));
        }

        return recyclerView;
    }

    @Override
    public void onDestroy() {
        postPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void appendNodes(final List<Tree.Node<Post>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(this.nodes.size() - 1, nodes);
        postAdapter.notifyItemRangeInserted(positionStart, nodes.size());
        loadingPosts = false;
    }

    @Override
    public Tree.Node<Post> popNode() {
        return popNode(nodes.size() - 1);
    }

    @Override
    public Tree.Node<Post> popNode(final int index) {
        final Tree.Node<Post> poppedNode = nodes.get(index);
        nodes.remove(index);
        postAdapter.notifyItemRemoved(index);
        return poppedNode;
    }

    @Override
    public void clearNodes() {
        final int nodesSize = this.nodes.size();
        this.nodes.clear();
        postAdapter.notifyItemRangeRemoved(0, nodesSize);
    }

    public Observable<Boolean> getNextPageTrigger() {
        return RxRecyclerView.scrollEvents(recyclerView)
                .filter(scrollEvent -> scrollEvent.dy() > 0) //check for scroll down
                .filter(scrollEvent -> !loadingPosts)
                .filter(scrollEvent -> {
                    final int visibleItemCount = recyclerView.getChildCount();
                    final int totalItemCount = layoutManager.getItemCount();
                    final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    return totalItemCount - visibleItemCount <= firstVisibleItem;
                })
                .map(scrollEvent -> {
                    loadingPosts = true;
                    return true;
                });
    }
}
