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
import com.veyndan.redditclient.UserFilter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.model.IndeterminateProgress;
import com.veyndan.redditclient.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.MarginItemDecoration;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;
import com.veyndan.redditclient.util.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;

public class PostsFragment extends Fragment implements PostMvpView<Response<Thing<Listing>>> {

    private static final String ARG_USER_FILTER = "user_filter";

    private final PostPresenter postPresenter = new PostPresenter();

    private RecyclerView recyclerView;

    private final List<Node<Response<Thing<Listing>>>> nodes = new ArrayList<>();

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
        postPresenter.loadNode(new IndeterminateProgress.Builder()
                .trigger(getTrigger())
                .request(filter.getRequestObservable(reddit))
                .build());
    }

    public void setFilter(final Observable<Response<List<Thing<Listing>>>> commentRequest) {
        clearNodes();
        postPresenter.loadNode(commentRequest, new IndeterminateProgress.Builder()
                .trigger(Observable.just(true))
                .build());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        postAdapter = new PostAdapter(getActivity(), nodes, reddit);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        recyclerView.addItemDecoration(new TreeInsetItemDecoration(getActivity(), R.dimen.post_child_inset_multiplier));
        recyclerView.setAdapter(postAdapter);

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        final Bundle args = getArguments();
        if (args != null) {
            setFilter((PostsFilter) args.getParcelable(ARG_USER_FILTER));
        }

        return recyclerView;
    }

    @Override
    public void onDestroy() {
        postPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void appendNode(final Node<Response<Thing<Listing>>> node) {
        nodes.add(node);
        postAdapter.notifyItemInserted(nodes.size() - 1);
        loadingPosts = false;
    }

    @Override
    public void appendNodes(final List<? extends Node<Response<Thing<Listing>>>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(nodes);
        postAdapter.notifyItemRangeInserted(positionStart, nodes.size());
        loadingPosts = false;
    }

    @Override
    public Node<Response<Thing<Listing>>> popNode() {
        return popNode(nodes.size() - 1);
    }

    @Override
    public Node<Response<Thing<Listing>>> popNode(final int index) {
        final Node<Response<Thing<Listing>>> poppedNode = nodes.get(index);
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

    private Observable<Boolean> getTrigger() {
        return Observable.concat(getFirstPageTrigger(), getNextPageTrigger());
    }

    private Observable<Boolean> getFirstPageTrigger() {
        return Observable.from(nodes).count().map(integer -> integer == 1);
    }

    private Observable<Boolean> getNextPageTrigger() {
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
