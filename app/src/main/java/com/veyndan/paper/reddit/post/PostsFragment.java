package com.veyndan.paper.reddit.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.veyndan.paper.reddit.Config;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.More;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.media.mutator.Mutators;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.post.model.Progress;
import com.veyndan.paper.reddit.util.Node;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class PostsFragment extends NodesFragment<Response<Thing<Listing>>> {

    private final Reddit reddit = new Reddit(Config.REDDIT_CREDENTIALS);

    private PostAdapter postAdapter;

    @SuppressWarnings("RedundantNoArgConstructor")
    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        postAdapter = new PostAdapter(getActivity(), getForest(), reddit);
    }

    @Override
    public RecyclerView.Adapter<?> getAdapter() {
        return postAdapter;
    }

    // TODO Don't pass in anything to do with Reddit e.g. here is the Thing<Listing>. Plus don't
    // pass in anything related to how we got the node i.e. here is Response. We should just pass
    // in a Node such that it is a generic fragment that can display nodes. Then this'll come in
    // handy when making other Paper apps as can pull this fragment out into library and then reuse
    // it. We will need a seperate class to act as the migration from Reddit api (or any other API)
    // to Node.
    public void setRequest(final Single<Response<Thing<Listing>>> request) {
        final Observable<Node<Response<Thing<Listing>>>> nodes = request
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                .flatMapObservable(thing -> Observable.fromIterable(thing.data.children)
                        .observeOn(Schedulers.computation())
                        .flatMapSingle(redditObject -> {
                            if (redditObject instanceof Submission) {
                                return Single.just(redditObject)
                                        .cast(Submission.class)
                                        .map(Post::new)
                                        .flatMap(Mutators.mutate());
                            } else if (redditObject instanceof More) {
                                final More more = (More) redditObject;
                                return Single.just(new Progress.Builder()
                                        .degree(more.count)
                                        .build());
                            } else {
                                throw new IllegalStateException("Unknown node class: " + redditObject);
                            }
                        })
                        .concatWith(Observable.just(new Progress.Builder()
                                .build())));

        setNode(nodes, new Progress.Builder().build());
    }
}
