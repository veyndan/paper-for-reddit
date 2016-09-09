package com.veyndan.redditclient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.post.PostAdapter;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CommentsFragment extends Fragment {

    @BindDimen(R.dimen.post_child_inset_multiplier) int childInsetMultiplier;

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        final List<Post> posts = new ArrayList<>();

        final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID_RAWJAVA, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
        final Reddit reddit = new Reddit.Builder(credentials).build();

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final TreeInsetItemDecoration treeInsetItemDecoration = new TreeInsetItemDecoration(childInsetMultiplier);
        final PostAdapter postAdapter = new PostAdapter(getActivity(), posts, reddit);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(treeInsetItemDecoration);
        recyclerView.setAdapter(postAdapter);

        EventBus.INSTANCE.toObserverable()
                .ofType(Post.class)
                .observeOn(Schedulers.io())
                .flatMap(post -> {
                    final String subreddit = post.getSubreddit();
                    final String fullname = post.getFullname();
                    final String article = fullname.substring(3, fullname.length());

                    return reddit.subredditComments(subreddit, article);
                })
                .map(Response::body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(things -> {
                    // No data is lost as both things.get(0) and things.get(1), which is all the
                    // things, has null set to before, after, and modhash in the Listing.
                    // things.get(0).data.children contains the Link.java only
                    final Tree<RedditObject> tree = new Tree<>(things.get(0).data.children.get(0), new ArrayList<>());
                    makeTree(tree, things.get(1));

                    for (final Tree<RedditObject> child : tree.getChildren()) {
                        child.generateDepths();
                    }

                    final List<Integer> depths = tree.toFlattenedDepthList();

                    treeInsetItemDecoration.setInsets(depths);
                    recyclerView.invalidateItemDecorations();

                    Observable.from(tree.toFlattenedDataList())
                            .ofType(Submission.class)
                            .observeOn(Schedulers.computation())
                            .map(Post::new)
                            .flatMap(Mutators.mutate())
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ps -> {
                                posts.addAll(ps);
                                postAdapter.notifyDataSetChanged();
                            }, Timber::e);
                }, Timber::e);

        return recyclerView;
    }

    private static void makeTree(final Tree<RedditObject> tree, final Thing<Listing> thing) {
        for (final RedditObject childData : thing.data.children) {
            final Tree<RedditObject> childTree = new Tree<>(childData, new ArrayList<>());
            tree.getChildren().add(childTree);

            if (childData instanceof Comment) {
                final Comment childComment = (Comment) childData;
                makeTree(childTree, childComment.getReplies());
            }
        }
    }
}
