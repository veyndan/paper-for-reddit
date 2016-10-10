package com.veyndan.redditclient.post;

import com.google.common.collect.FluentIterable;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.DeterminateProgress;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.util.Tree;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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

        Observable.from(nodes)
                .flatMap(Node::asObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .filter(nodes1 -> !nodes1.isEmpty())
                .doOnNext(nodes1 -> postMvpView.popNode())
                .flatMap(Observable::from)
                .map(Tree::flattenFrom)
                .subscribe(this::loadNodes);
    }

    public void loadNode(final Observable<Response<List<Thing<Listing>>>> commentRequest,
                         final Node<Response<Thing<Listing>>> node) {
        postMvpView.appendNode(node);

        node.getTrigger().takeFirst(Boolean::booleanValue)
                .flatMap(aBoolean -> commentRequest.subscribeOn(Schedulers.io()))
                .map(Response::body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(things -> {
                    // No data is lost as both things.get(0) and things.get(1), which is all the
                    // things, has null set to before, after, and modhash in the Listing.
                    // things.get(0).data.children contains the Link.java only.

                    // Here I am setting an actual tree where the link is the root node, and
                    // the root comments are the children of the link.
                    final Post root = new Post((Submission) things.get(0).data.children.get(0));

                    root.getChildren().addAll(FluentIterable.from(things.get(1).data.children).transform(input -> {
                        if (input instanceof Submission) {
                            final Post[] outerPost = new Post[1];
                            Observable.just(input)
                                    .cast(Submission.class)
                                    .map(Post::new)
                                    .flatMap(Mutators.mutate())
                                    .subscribe(post -> {
                                        outerPost[0] = post;
                                    });
                            return outerPost[0];
                        } else if (input instanceof More) {
                            final More more = (More) input;
                            return new DeterminateProgress.Builder()
                                    .trigger(Observable.just(true))
                                    .childCount(more.count)
                                    .build();
                        } else {
                            throw new IllegalStateException("Unknown node class: " + input);
                        }
                    }).toList());

                    postMvpView.popNode();
                    postMvpView.appendNodes(Tree.flattenFrom(root));
                }, Timber::e);
    }
}
