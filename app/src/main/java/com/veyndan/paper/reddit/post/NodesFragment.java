package com.veyndan.paper.reddit.post;

import android.os.Bundle;
import android.support.annotation.CallSuper;
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
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.MarginItemDecoration;
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import timber.log.Timber;

public abstract class NodesFragment<T> extends Fragment {

    private RecyclerView recyclerView;

    private final List<Node<T>> forest = new ArrayList<>();

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

    protected final void setNode(final Observable<Node<T>> nodes, final Node<T> intialNode) {
        clearForest();

        final Observable<NextPageEvent<T>> nextPageEvents = RxRecyclerView.scrollEvents(recyclerView)
                .filter(endOfRecyclerView())
                // TODO There should be a more robust and intuitive way of doing distinctUntilChanged().
                //
                //      When to solve: After nodes is no longer a flattened tree but is instead a
                //      indexable tree.
                .map(scrollEvent -> new NextPageEvent<>(forest.get(forest.size() - 1)))
                .distinctUntilChanged(event -> forest.size() - 1);

        final ObservableTransformer<NextPageEvent<T>, NextPageUiModel<T>> nextPage = events -> events
                .flatMapSingle(event -> nodes
                        .observeOn(AndroidSchedulers.mainThread())
                        .concatMap(tree1 -> tree1.preOrderTraverse(0))
                        .toList())
                .map(NextPageUiModel::forest)
                .startWith(NextPageUiModel.<T>tree(intialNode));

        nextPageEvents.compose(nextPage)
                .subscribe(model -> {
                    // TODO Logic: The below assumes that the last element is the one to be replaced (i.e. event.getNode())
                    // though it should allow any node i.e. for the comment section.
                    Timber.d("Next page");
                    if (forest.size() > 0) { // TODO Code smell: This is done as startWith is called above.
                        popTree();
                    }
                    appendForest(model.getForest());
                }, Timber::e);
    }

    @CallSuper
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_nodes, container, false);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        recyclerView.addItemDecoration(new TreeInsetItemDecoration(getActivity(), R.dimen.post_child_inset_multiplier));
        recyclerView.setAdapter(getAdapter());

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return recyclerView;
    }

    protected final List<Node<T>> getForest() {
        return forest;
    }

    private void appendTree(final Node<T> node) {
        forest.add(node);
        getAdapter().notifyItemInserted(forest.size() - 1);
    }

    private void appendForest(final List<? extends Node<T>> forest) {
        final int positionStart = this.forest.size();
        this.forest.addAll(forest);
        getAdapter().notifyItemRangeInserted(positionStart, forest.size());
    }

    private Node<T> popTree() {
        return popTree(forest.size() - 1);
    }

    private Node<T> popTree(final int index) {
        final Node<T> poppedTree = forest.get(index);
        forest.remove(index);
        getAdapter().notifyItemRemoved(index);
        return poppedTree;
    }

    private void clearForest() {
        final int treeCount = forest.size();
        forest.clear();
        getAdapter().notifyItemRangeRemoved(0, treeCount);
    }

    public abstract RecyclerView.Adapter<?> getAdapter();
}
