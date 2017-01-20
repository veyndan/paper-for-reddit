package com.veyndan.paper.reddit.post;

import com.veyndan.paper.reddit.ForestModel;
import com.veyndan.paper.reddit.Presenter;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Response;

public class PostPresenter implements Presenter<PostMvpView<Response<Thing<Listing>>, ForestModel<Response<Thing<Listing>>>>> {

    private PostMvpView<Response<Thing<Listing>>, ForestModel<Response<Thing<Listing>>>> postMvpView;

    @Override
    public void attachView(final PostMvpView<Response<Thing<Listing>>, ForestModel<Response<Thing<Listing>>>> view) {
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
        // TODO Pass in ForestModel with whole state to render.
        //  ??? How do I get the forest as the parameter is only giving this function what
        //      is to be appended.
        //  ANS This functions is defined recursively, so the first time this is called outside
        //      itself is actually all the nodes that the Reddit feed is showing. When called
        //      recursively, it is kind of "appending to the back stack" of nodes. Moving this
        //      recursion to Reddit.java such that called reddit.subreddit(…) will return every
        //      post in the subreddit. You limit it by using Observable#take. This is a feature
        //      which has been thought about before.
        //  ??? Should I be passing the ForestModel here or somewhere else? This is a cycle in
        //      MVI but calling it *Presenter is confusing.
        //  ANS Better understanding and implementation of MVI is needed.
        postMvpView.appendNodes(nodes);

        // TODO Must fix the below first as otherwise doing Observable.fromIterable(nodes) is
        //      thought to be Observable.fromIterable(forestModel.getTrees()). If recursion wasn't
        //      here that would be true, otherwise at them moment iterating through all the trees
        //      would occur each page load instead of just the appended trees.
        //      Remember that you *may* have to think about it as 2d instead of 1d as the Observable
        //      shouldn't just be done one after the other, as sometimes children of a node is
        //      loaded, which is almost back in time, i.e. getting the comments of a post. Maybe,
        //      have to consider it as Observable<Observable> in the stream, but then won't it be
        //      Observable<Observable<Observable>> then Observable<Observable<Observable<Observable>>>
        //      as you go each node deeper. Remember getChildren() though is in node which is an
        //      Observable so kind of already defined? This is more that I don't have a full grasp
        //      of the system, so it needs to be understood and simplified i.e. recursion removal,
        //      potentially using subjects for triggers (though won't it be bidirectional then,
        //      which is against MVI cycles as they should be unidirectional?).
        //      Actually I haven't thought of it as 2d but it is 1d, that is why pagination of
        //      comments doesn't work. In preOrderTraverse method there is also recursion which
        //      is doing the 2d stuff, but none of them are coming back here to do recursive pagination
        //      here.
        Observable.fromIterable(nodes)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(node -> node.getTrigger()
                        .filter(Boolean::booleanValue)
                        .firstElement()
                        .flatMapObservable(aBoolean -> node.asObservable()))
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(node -> node.preOrderTraverse(0))
                .toList()
                .subscribe(nodes1 -> {
                    postMvpView.popNode();
                    loadNodes(nodes1);
                });
    }
}
