package com.veyndan.redditclient.post;

import android.util.Pair;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.Stub;

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

    public void loadNodes(final PostsFilter filter, final Observable<Boolean> trigger) {
        postMvpView.appendNode(new Tree.Node<>(new Stub()));

        trigger.takeFirst(Boolean::booleanValue)
                .flatMap(aBoolean -> filter.getRequestObservable(reddit).subscribeOn(Schedulers.io()))
                .observeOn(Schedulers.computation())
                .map(Response::body)
                .flatMap(thing -> Observable.from(thing.data.children)
                                .cast(Submission.class)
                                .map(Post::new)
                                .flatMap(Mutators.mutate())
                                .map(Tree.Node::new)
                                .toList(),
                        (thing, nodes) -> new Pair<>(thing.data.after, nodes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    final String after = pair.first;
                    final List<Tree.Node<Post>> nodes = pair.second;

                    postMvpView.popNode();
                    postMvpView.appendNodes(nodes);

                    if (after != null) {
                        filter.setAfter(after);
                        loadNodes(filter, trigger);
                    }
                });
    }

    public void loadNodes(final Observable<Response<List<Thing<Listing>>>> commentRequest) {
        postMvpView.appendNode(new Tree.Node<>(new Stub()));

        commentRequest
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(things -> {
                    // No data is lost as both things.get(0) and things.get(1), which is all the
                    // things, has null set to before, after, and modhash in the Listing.
                    // things.get(0).data.children contains the Link.java only.

                    // Here I am setting an actual tree where the link is the root node, and
                    // the root comments are the children of the link.
                    final Post root = new Post((Submission) things.get(0).data.children.get(0));
                    root.getReplies().data.children.addAll(things.get(1).data.children);

                    final Tree<Object> tree = new Tree<>(new Tree.Node<>(root), new ArrayList<>());
                    makeTree(tree, root.getReplies());

                    postMvpView.popNode();
                    postMvpView.appendNodes(tree.toFlattenedNodeList());
                }, Timber::e);
    }

    private static void makeTree(final Tree<Object> tree, final Thing<Listing> thing) {
        for (final RedditObject childData : thing.data.children) {
            if (childData instanceof Submission) {
                Observable.just(childData)
                        .cast(Submission.class)
                        .map(Post::new)
                        .flatMap(Mutators.mutate())
                        .subscribe(post -> {
                            final Tree<Object> childTree = new Tree<>(new Tree.Node<>(post), new ArrayList<>());
                            tree.getChildren().add(childTree);
                            makeTree(childTree, post.getReplies());
                        });
            } else if (childData instanceof More) {
                final More more = (More) childData;

                final Tree<Object> childTree = new Tree<>(new Tree.Node<>(new Stub(more.count)), new ArrayList<>());
                tree.getChildren().add(childTree);
            } else {
                throw new IllegalStateException("Unknown node class: " + childData);
            }
        }
    }
}
