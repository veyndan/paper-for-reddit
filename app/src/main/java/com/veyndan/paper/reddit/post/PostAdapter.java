package com.veyndan.paper.reddit.post;

import android.app.Activity;
import android.support.annotation.IntRange;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.post.delegate.MoreAdapterDelegate;
import com.veyndan.paper.reddit.post.delegate.PostAdapterDelegate;
import com.veyndan.paper.reddit.post.delegate.ProgressAdapterDelegate;
import com.veyndan.paper.reddit.util.Node;

import java.util.List;

import retrofit2.Response;

public class PostAdapter extends ListDelegationAdapter<List<Node<Response<Thing<Listing>>>>> {

    public PostAdapter(final Activity activity, final List<Node<Response<Thing<Listing>>>> posts, final Reddit reddit) {
        delegatesManager
                .addDelegate(new PostAdapterDelegate(this, activity, reddit))
                .addDelegate(new ProgressAdapterDelegate())
                .addDelegate(new MoreAdapterDelegate());

        setItems(posts);
    }
}
