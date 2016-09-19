package com.veyndan.redditclient.post;

import android.util.Pair;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
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
                    // things.get(0).data.children contains the Link.java only
                    final Tree<RedditObject> tree = new Tree<>(new Tree.Node<>(things.get(0).data.children.get(0)), new ArrayList<>());
                    makeTree(tree, things.get(1));

                    for (final Tree<RedditObject> child : tree.getChildren()) {
                        child.generateDepths();
                    }

                    Observable.from(tree.toFlattenedNodeList())
                            .concatMap(node -> {
                                if (node.getData() instanceof Submission) {
                                    return Observable.just(node)
                                            .map(Tree.Node::getData)
                                            .map(Post::new)
                                            .flatMap(Mutators.mutate())
                                            .map(p -> new Tree.Node<>(p, node.getDepth()));
                                } else if (node.getData() instanceof More) {
                                    final More more = (More) node.getData();
                                    return Observable.just(new Tree.Node<>(new Stub(more.count), node.getDepth()));
                                } else {
                                    return Observable.error(new IllegalStateException("Unknown node class: " + node.getData()));
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(posts -> {
                                postMvpView.popNode();
                                postMvpView.appendNodes(posts);
                            }, Timber::e);
                }, Timber::e);
    }

    private static void makeTree(final Tree<RedditObject> tree, final Thing<Listing> thing) {
        for (final RedditObject childData : thing.data.children) {
            final Tree<RedditObject> childTree = new Tree<>(new Tree.Node<>(childData), new ArrayList<>());
            tree.getChildren().add(childTree);

            if (childData instanceof Comment) {
                final Comment childComment = (Comment) childData;
                makeTree(childTree, childComment.getReplies());
            }
        }
    }
}
