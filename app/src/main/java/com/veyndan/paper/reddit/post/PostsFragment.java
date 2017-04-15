package com.veyndan.paper.reddit.post;

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

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.veyndan.paper.reddit.Config;
import com.veyndan.paper.reddit.NextPageEvent;
import com.veyndan.paper.reddit.NextPageUiModel;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.MarginItemDecoration;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class PostsFragment extends Fragment {

    private RecyclerView recyclerView;

    private final List<Node<Response<Thing<Listing>>>> nodes = new ArrayList<>();

    private PostAdapter postAdapter;

    private Reddit reddit;

    @SuppressWarnings("RedundantNoArgConstructor")
    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        reddit = new Reddit(Config.REDDIT_CREDENTIALS);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private static Predicate<RecyclerViewScrollEvent> endOfRecyclerView() {
        return scrollEvent -> {
            // Scroll down: scrollEvent.dy() > 0
            // Initial load: scrollEvent.dy() == 0
            if (scrollEvent.dy() >= 0) {
                final RecyclerView recyclerView = scrollEvent.view();
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                final int visibleItemCount = recyclerView.getChildCount();
                final int totalItemCount = layoutManager.getItemCount();
                final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                return totalItemCount - visibleItemCount <= firstVisibleItem;
            }

            return false;
        };
    }

    public void setRequest(final Single<Response<Thing<Listing>>> request) {
        clearNodes();

        final Observable<NextPageEvent> nextPageEvents = RxRecyclerView.scrollEvents(recyclerView)
                .filter(endOfRecyclerView())
                // TODO There should be a more robust and intuitive way of doing distinctUntilChanged().
                //
                //      When to solve: After nodes is no longer a flattened tree but is instead a
                //      indexable tree.
                .map(scrollEvent -> nodes.size() - 1)
                .distinctUntilChanged()
                .map(position -> new NextPageEvent(nodes.get(position)));

        final ObservableTransformer<NextPageEvent, NextPageUiModel> nextPage = events -> events
                .map(NextPageEvent::getNode)
                .flatMap(node -> request
                        .subscribeOn(Schedulers.io())
                        .map(Response::body)
                        .toObservable()
                        .flatMap(thing -> Observable.fromIterable(thing.data.children)
                                .observeOn(Schedulers.computation())
                                .flatMapSingle(redditObject -> {
                                    if (redditObject instanceof Submission) {
                                        return Single.just(redditObject)
                                                .cast(Submission.class)
                                                .map(Post::new)
                                                .flatMap(Mutators.mutate());
                                    } else if (redditObject instanceof More) {
                                        final More more = (More) redditObject;
                                        return Single.just(new Progress.Builder()
                                                .degree(more.count)
                                                .build());
                                    } else {
                                        throw new IllegalStateException("Unknown node class: " + redditObject);
                                    }
                                })
                                .concatWith(Observable.just(new Progress.Builder()
                                        .build())))
                        .observeOn(AndroidSchedulers.mainThread())
                        .concatMap(node1 -> node1.preOrderTraverse(0))
                        .toList()
                        .toObservable())
                .map(NextPageUiModel::nodes)
                .startWith(NextPageUiModel.node(new Progress.Builder()
                        .build()));

        nextPageEvents.compose(nextPage)
                .subscribe(model -> {
                    // TODO Logic: The below assumes that the last element is the one to be replaced (i.e. event.getNode())
                    // though it should allow any node i.e. for the comment section.
                    Timber.d("Next page");
                    if (nodes.size() > 0) { // TODO Code smell: This is done as startWith is called above.
                        popNode();
                    }
                    appendNodes(model.getNodes());
                }, Timber::e);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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

    public void appendNode(final Node<Response<Thing<Listing>>> node) {
        nodes.add(node);
        postAdapter.notifyItemInserted(nodes.size() - 1);
    }

    public void appendNodes(final List<? extends Node<Response<Thing<Listing>>>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(nodes);
        postAdapter.notifyItemRangeInserted(positionStart, nodes.size());
    }

    public Node<Response<Thing<Listing>>> popNode() {
        return popNode(nodes.size() - 1);
    }

    public Node<Response<Thing<Listing>>> popNode(final int index) {
        final Node<Response<Thing<Listing>>> poppedNode = nodes.get(index);
        nodes.remove(index);
        postAdapter.notifyItemRemoved(index);
        return poppedNode;
    }

    public void clearNodes() {
        final int nodesSize = nodes.size();
        nodes.clear();
        postAdapter.notifyItemRangeRemoved(0, nodesSize);
    }
}
