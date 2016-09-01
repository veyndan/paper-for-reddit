package com.veyndan.redditclient.post;

import android.app.Activity;

import com.hannesdorfmann.adapterdelegates2.ListDelegationAdapter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.delegate.PostAdapterDelegate;
import com.veyndan.redditclient.post.delegate.ProgressAdapterDelegate;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public class PostAdapter extends ListDelegationAdapter<List<Post>> {

    public PostAdapter(final Activity activity, final List<Post> posts, final Reddit reddit,
                       final int width) {
        delegatesManager
                .addDelegate(new PostAdapterDelegate(this, activity, reddit, width))
                .addDelegate(new ProgressAdapterDelegate());

        setItems(posts);
    }
}
