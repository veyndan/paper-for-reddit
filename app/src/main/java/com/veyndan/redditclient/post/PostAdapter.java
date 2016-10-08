package com.veyndan.redditclient.post;

import android.app.Activity;
import android.support.annotation.IntRange;

import com.hannesdorfmann.adapterdelegates2.ListDelegationAdapter;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.util.Node;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.delegate.MoreAdapterDelegate;
import com.veyndan.redditclient.post.delegate.PostAdapterDelegate;
import com.veyndan.redditclient.post.delegate.ProgressAdapterDelegate;

import java.util.List;

import retrofit2.Response;

public class PostAdapter extends ListDelegationAdapter<List<Node<Response<Thing<Listing>>>>>
        implements DepthCalculatorDelegate {

    public PostAdapter(final Activity activity, final List<Node<Response<Thing<Listing>>>> posts, final Reddit reddit) {
        delegatesManager
                .addDelegate(new PostAdapterDelegate(this, activity, reddit))
                .addDelegate(new ProgressAdapterDelegate())
                .addDelegate(new MoreAdapterDelegate());

        setItems(posts);
    }

    @Override
    @IntRange(from = 0)
    public int depthForPosition(@IntRange(from = 0) final int position) {
        return items.get(position).getDepth();
    }
}
