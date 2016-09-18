package com.veyndan.redditclient.post;

import android.util.Pair;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.Presenter;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        postMvpView.appendNode(new Tree.Node<>(null, Tree.Node.TYPE_PROGRESS));

        trigger.takeFirst(Boolean::booleanValue)
                .flatMap(aBoolean -> filter.getRequestObservable(reddit).subscribeOn(Schedulers.io()))
                .observeOn(Schedulers.computation())
                .map(Response::body)
                .flatMap(thing -> Observable.from(thing.data.children)
                                .map(Post::new)
                                .flatMap(Mutators.mutate())
                                .map(post -> new Tree.Node<>(post, Tree.Node.TYPE_CONTENT))
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
}
