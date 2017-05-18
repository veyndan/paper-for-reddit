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
import com.veyndan.paper.reddit.NextPageEvent;
import com.veyndan.paper.reddit.NextPageUiModel;
import com.veyndan.paper.reddit.PaperForRedditApp;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.databinding.FragmentPostsBinding;
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

    private FragmentPostsBinding binding;

    private final List<Node<Response<Thing<Listing>>>> forest = new ArrayList<>();

    private PostAdapter postAdapter;

    private Reddit reddit;

    @SuppressWarnings("RedundantNoArgConstructor")
    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        reddit = new Reddit(PaperForRedditApp.Companion.getREDDIT_CREDENTIALS());
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
        clearForest();

        final Observable<NextPageEvent> nextPageEvents = RxRecyclerView.scrollEvents(binding.recyclerView)
                .filter(endOfRecyclerView())
                // TODO There should be a more robust and intuitive way of doing distinctUntilChanged().
                //
                //      When to solve: After nodes is no longer a flattened tree but is instead a
                //      indexable tree.
                .map(scrollEvent -> new NextPageEvent(forest.get(forest.size() - 1)))
                .distinctUntilChanged(event -> forest.size() - 1);

        final ObservableTransformer<NextPageEvent, NextPageUiModel> nextPage = events -> events
                .flatMapSingle(event -> request
                        .subscribeOn(Schedulers.io())
                        .map(Response::body)
                        .flattenAsObservable(thing -> thing.getData().getChildren())
                        .observeOn(Schedulers.computation())
                        .flatMapSingle(redditObject -> {
                            if (redditObject instanceof Submission) {
                                return Single.just(redditObject)
                                        .cast(Submission.class)
                                        .map(Post::create)
                                        .flatMap(Mutators.mutate());
                            } else if (redditObject instanceof More) {
                                final More more = (More) redditObject;
                                return Single.just(new Progress(more.getCount()));
                            } else {
                                final String message = "Unknown node class: " + redditObject;
                                return Single.error(new IllegalStateException(message));
                            }
                        })
                        .concatWith(Observable.just(new Progress()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .concatMap(tree1 -> tree1.preOrderTraverse(0))
                        .toList())
                .map(NextPageUiModel::new)
                .startWith(new NextPageUiModel(new Progress()));

        nextPageEvents.compose(nextPage)
                .subscribe(model -> {
                    // TODO Logic: The below assumes that the last element is the one to be replaced (i.e. event.getNode())
                    // though it should allow any node i.e. for the comment section.
                    Timber.d("Next page");
                    if (forest.size() > 0) { // TODO Code smell: This is done as startWith is called above.
                        popTree();
                    }
                    appendForest(model.getForest());
                });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postAdapter = new PostAdapter(getActivity(), forest, reddit);

        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        binding.recyclerView.addItemDecoration(new TreeInsetItemDecoration(getActivity(), R.dimen.post_child_inset_multiplier));
        binding.recyclerView.setAdapter(postAdapter);

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        return binding.getRoot();
    }

    public void appendTree(final Node<Response<Thing<Listing>>> node) {
        forest.add(node);
        postAdapter.notifyItemInserted(forest.size() - 1);
    }

    public void appendForest(final List<? extends Node<Response<Thing<Listing>>>> forest) {
        final int positionStart = this.forest.size();
        this.forest.addAll(forest);
        postAdapter.notifyItemRangeInserted(positionStart, forest.size());
    }

    public Node<Response<Thing<Listing>>> popTree() {
        return popTree(forest.size() - 1);
    }

    public Node<Response<Thing<Listing>>> popTree(final int index) {
        final Node<Response<Thing<Listing>>> poppedTree = forest.get(index);
        forest.remove(index);
        postAdapter.notifyItemRemoved(index);
        return poppedTree;
    }

    public void clearForest() {
        final int treeCount = forest.size();
        forest.clear();
        postAdapter.notifyItemRangeRemoved(0, treeCount);
    }
}
