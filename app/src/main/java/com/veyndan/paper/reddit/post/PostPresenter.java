package com.veyndan.paper.reddit.post;

import com.veyndan.paper.reddit.Presenter;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Response;
import timber.log.Timber;

public class PostPresenter implements Presenter<PostMvpView<Response<Thing<Listing>>>> {

    private PostMvpView<Response<Thing<Listing>>> postMvpView;

    @Override
    public void attachView(final PostMvpView<Response<Thing<Listing>>> view) {
        postMvpView = view;
    }

    @Override
    public void detachView() {
        postMvpView = null;
    }

    public void loadNode(final Node<Response<Thing<Listing>>> node) {
        loadNodes(Collections.singletonList(node));
    }

    public void loadNodes(final List<Node<Response<Thing<Listing>>>> nodes) {
        postMvpView.appendNodes(nodes);

        Observable.fromIterable(nodes)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(node -> node.getTrigger()
                        .filter(Boolean::booleanValue)
                        .firstElement()
                        .flatMapObservable(aBoolean -> node.replacementNodes()))
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(node -> node.preOrderTraverse(0))
                .toList()
                .subscribe(nodes1 -> {
                    Timber.d(nodes1.toString());
                    postMvpView.clearNodes();
                    loadNodes(nodes1);
                });
    }
}
