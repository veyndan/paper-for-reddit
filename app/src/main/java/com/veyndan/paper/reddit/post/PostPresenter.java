package com.veyndan.paper.reddit.post;

import com.veyndan.paper.reddit.Presenter;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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
                .flatMap(node -> node.getEvents()
                        .filter(Boolean::booleanValue)
                        .firstElement()
                        .flatMapObservable(aBoolean -> node.getRequest()
                                .subscribeOn(Schedulers.io())
                                .map(Response::body)
                                .toObservable()
                                .flatMap(thing -> Observable.fromIterable(thing.data.children)
                                        .observeOn(Schedulers.computation())
                                        .concatMap(redditObject -> {
                                            if (redditObject instanceof Submission) {
                                                return Single.just(redditObject)
                                                        .cast(Submission.class)
                                                        .map(Post::new)
                                                        .flatMap(Mutators.mutate())
                                                        .toObservable();
                                            } else if (redditObject instanceof More) {
                                                final More more = (More) redditObject;
                                                return Single.just(new Progress.Builder()
                                                        .events(Observable.just(true))
                                                        .degree(more.count)
                                                        .build())
                                                        .toObservable();
                                            } else {
                                                throw new IllegalStateException("Unknown node class: " + redditObject);
                                            }
                                        })
                                        .concatWith(Observable.just(new Progress.Builder()
                                                .events(node.getEvents())
                                                .request(node.getRequest())
                                                .build())))))
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(node -> node.preOrderTraverse(0))
                .toList()
                .subscribe(nodes1 -> {
                    postMvpView.popNode();
                    loadNodes(nodes1);
                }, Timber::e);
    }
}
