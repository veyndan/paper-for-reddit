package com.veyndan.redditclient.post;

import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.util.Tree;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

        Observable.from(nodes)
                .flatMap(Node::asObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .filter(nodes1 -> !nodes1.isEmpty())
                .doOnNext(nodes1 -> postMvpView.popNode())
                .flatMap(Observable::from)
                .concatMap(node -> Tree.flattenFrom(Observable.just(node), 0))
                .toList()
                .subscribe(this::loadNodes);
    }
}
