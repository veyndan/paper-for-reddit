package com.veyndan.redditclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.Credentials;

import java.util.ArrayList;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CommentsFragment extends Fragment {

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final Credentials credentials = new Credentials(Config.REDDIT_CLIENT_ID_RAWJAVA, Config.REDDIT_CLIENT_SECRET, Config.REDDIT_USER_AGENT, Config.REDDIT_USERNAME, Config.REDDIT_PASSWORD);
        final Reddit reddit = new Reddit.Builder(credentials).build();

        reddit.subredditComments("funny", "51mbgd")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Response::body)
                .subscribe(things -> {
                    // No data is lost as both things.get(0) and things.get(1), which is all the
                    // things, has null set to before, after, and modhash in the Listing.
                    // things.get(0).data.children contains the Link.java only
                    final Tree<RedditObject> tree = new Tree<>(things.get(0).data.children.get(0), new ArrayList<>());
                    makeTree(tree, things.get(1));

                    Timber.d(tree.toString());

                }, Timber::e);

        return inflater.inflate(R.layout.fragment_comments, container, false);
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
