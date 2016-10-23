package com.veyndan.paper.reddit.tree;

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
import com.veyndan.paper.reddit.Config;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.tree.adapter.TreeAdapter;
import com.veyndan.paper.reddit.tree.model.Progress;
import com.veyndan.paper.reddit.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.MarginItemDecoration;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;

public class PostsFragment extends Fragment implements PostMvpView<Response<Thing<Listing>>> {

    private final PostPresenter postPresenter = new PostPresenter();

    private RecyclerView recyclerView;

    private final List<Node<Response<Thing<Listing>>>> nodes = new ArrayList<>();

    private TreeAdapter treeAdapter;

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;

    private Reddit reddit;

    @SuppressWarnings("RedundantNoArgConstructor")
    public PostsFragment() {
        // Required empty public constructor
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

    public void setRequest(final Observable<Response<Thing<Listing>>> request) {
        clearNodes();
        postPresenter.loadNode(new Progress.Builder()
                .trigger(getTrigger())
                .request(request)
                .build());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        treeAdapter = new TreeAdapter(getActivity(), nodes, reddit);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        recyclerView.setAdapter(treeAdapter);

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
        treeAdapter.notifyItemInserted(nodes.size() - 1);
        loadingPosts = false;
    }

    @Override
    public void appendNodes(final List<? extends Node<Response<Thing<Listing>>>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(nodes);
        treeAdapter.notifyItemRangeInserted(positionStart, nodes.size());
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
        treeAdapter.notifyItemRemoved(index);
        return poppedNode;
    }

    @Override
    public void clearNodes() {
        final int nodesSize = nodes.size();
        nodes.clear();
        treeAdapter.notifyItemRangeRemoved(0, nodesSize);
    }

    private Observable<Boolean> getTrigger() {
        return Observable.concat(getFirstPageTrigger(), getNextPageTrigger())
                .filter(Boolean::booleanValue)
                .filter(aBoolean -> !loadingPosts)
                .doOnNext(aBoolean -> loadingPosts = true);
    }

    private Observable<Boolean> getFirstPageTrigger() {
        return Observable.from(nodes).count().map(integer -> integer == 1);
    }

    private Observable<Boolean> getNextPageTrigger() {
        return RxRecyclerView.scrollEvents(recyclerView)
                .filter(scrollEvent -> scrollEvent.dy() > 0) //check for scroll down
                .map(scrollEvent -> {
                    final int visibleItemCount = recyclerView.getChildCount();
                    final int totalItemCount = layoutManager.getItemCount();
                    final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    return totalItemCount - visibleItemCount <= firstVisibleItem;
                });
    }
}
