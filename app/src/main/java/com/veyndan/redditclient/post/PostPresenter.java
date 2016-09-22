package com.veyndan.redditclient.post;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.common.collect.FluentIterable;
import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.Stub;
import com.veyndan.redditclient.util.DepthTreeTraverser;
import com.veyndan.redditclient.util.Node;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostPresenter implements Presenter<PostMvpView> {

    private PostMvpView postMvpView;

    private final Reddit reddit;

    public PostPresenter() {
        reddit = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();
    }

    @Override
    public void attachView(final PostMvpView view) {
        postMvpView = view;
    }

    @Override
    public void detachView() {
        postMvpView = null;
    }

    public void loadNodes(final PostsFilter filter, final Stub stub) {
        postMvpView.appendNode(new Node<>(stub));

        stub.getTrigger().takeFirst(Boolean::booleanValue)
                .flatMap(aBoolean -> filter.getRequestObservable(reddit).subscribeOn(Schedulers.io()))
                .observeOn(Schedulers.computation())
                .map(Response::body)
                .flatMap(thing -> Observable.from(thing.data.children)
                                .cast(Submission.class)
                                .map(Post::new)
                                .flatMap(Mutators.mutate())
                                .map(Node::new)
                                .toList(),
                        (thing, nodes) -> new Pair<>(thing.data.after, nodes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    final String after = pair.first;
                    final List<Node<Post>> nodes = pair.second;

                    postMvpView.popNode();
                    postMvpView.appendNodes(nodes);

                    if (after != null) {
                        filter.setAfter(after);
                        loadNodes(filter, stub);
                    }
                });
    }

    public void loadNodes(final Observable<Response<List<Thing<Listing>>>> commentRequest,
                          final Stub stub) {
        postMvpView.appendNode(new Node<>(stub));

        stub.getTrigger().takeFirst(Boolean::booleanValue)
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

                    root.getReplies().addAll(FluentIterable.from(things.get(1).data.children).transform(input -> {
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
                            return new Stub.Builder(Observable.just(true)).childCount(more.count).build();
                        } else {
                            throw new IllegalStateException("Unknown node class: " + input);
                        }
                    }).toList());

                    final DepthTreeTraverser<Object> treeTraverser = new DepthTreeTraverser<Object>() {
                        @Override
                        public Iterable<Object> children(@NonNull final Object root) {
                            if (root instanceof Post) {
                                return ((Post) root).getReplies();
                            } else if (root instanceof Stub) {
                                return new ArrayList<>();
                            } else {
                                throw new IllegalStateException("Unknown node class: " + root);
                            }
                        }
                    };

                    postMvpView.popNode();
                    postMvpView.appendNodes(treeTraverser.preOrderTraversal(root).toList());
                }, Timber::e);
    }
}
