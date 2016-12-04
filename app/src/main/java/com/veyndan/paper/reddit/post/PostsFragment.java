package com.veyndan.paper.reddit.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.MarginItemDecoration;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

public class PostsFragment extends Fragment implements PostMvpView<Response<Thing<Listing>>> {

    @NonNull private final PostPresenter postPresenter = new PostPresenter();

    @Nullable private RecyclerView recyclerView;

    @NonNull private final List<Node<Response<Thing<Listing>>>> nodes = new ArrayList<>();

    @Nullable private PostAdapter postAdapter;

    @Nullable private LinearLayoutManager layoutManager;

    private boolean loadingPosts;

    @Nullable private Reddit reddit;

    @SuppressWarnings("RedundantNoArgConstructor")
    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        postPresenter.attachView(this);

        reddit = new Reddit(Config.REDDIT_CREDENTIALS);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setRequest(@NonNull final Single<Response<Thing<Listing>>> request) {
        clearNodes();
        postPresenter.loadNode(new Progress.Builder()
                .trigger(getTrigger())
                .request(request.toMaybe())
                .build());
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);

        layoutManager = new LinearLayoutManager(getActivity());
        postAdapter = new PostAdapter(getActivity(), nodes, reddit);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        recyclerView.addItemDecoration(new TreeInsetItemDecoration(getActivity(), R.dimen.post_child_inset_multiplier));
        recyclerView.setAdapter(postAdapter);

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
    public void appendNode(@NonNull final Node<Response<Thing<Listing>>> node) {
        nodes.add(node);
        postAdapter.notifyItemInserted(nodes.size() - 1);
        loadingPosts = false;
    }

    @Override
    public void appendNodes(@NonNull final List<? extends Node<Response<Thing<Listing>>>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(nodes);
        postAdapter.notifyItemRangeInserted(positionStart, nodes.size());
        loadingPosts = false;
    }

    @NonNull
    @Override
    public Node<Response<Thing<Listing>>> popNode() {
        return popNode(nodes.size() - 1);
    }

    @NonNull
    @Override
    public Node<Response<Thing<Listing>>> popNode(final int index) {
        final Node<Response<Thing<Listing>>> poppedNode = nodes.get(index);
        nodes.remove(index);
        postAdapter.notifyItemRemoved(index);
        return poppedNode;
    }

    @Override
    public void clearNodes() {
        final int nodesSize = nodes.size();
        nodes.clear();
        postAdapter.notifyItemRangeRemoved(0, nodesSize);
    }

    @NonNull
    private Observable<Boolean> getTrigger() {
        return Observable.concat(getFirstPageTrigger(), getNextPageTrigger())
                .filter(Boolean::booleanValue)
                .filter(aBoolean -> !loadingPosts)
                .doOnNext(aBoolean -> loadingPosts = true);
    }

    @NonNull
    private Observable<Boolean> getFirstPageTrigger() {
        return Observable.fromIterable(nodes)
                .count()
                .map(count -> count == 1)
                .toObservable();
    }

    @NonNull
    private Observable<Boolean> getNextPageTrigger() {
        return RxJavaInterop.toV2Observable(
                RxRecyclerView.scrollEvents(recyclerView)
                        .filter(scrollEvent -> scrollEvent.dy() > 0) //check for scroll down
                        .map(scrollEvent -> {
                            final int visibleItemCount = recyclerView.getChildCount();
                            final int totalItemCount = layoutManager.getItemCount();
                            final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                            return totalItemCount - visibleItemCount <= firstVisibleItem;
                        })
        );
    }
}
