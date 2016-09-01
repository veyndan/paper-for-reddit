package com.veyndan.redditclient.post;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates2.AdapterDelegatesManager;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.post.delegate.PostAdapterDelegate;
import com.veyndan.redditclient.post.delegate.ProgressAdapterDelegate;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final AdapterDelegatesManager<List<Post>> delegatesManager;

    private final List<Post> posts;

    public PostAdapter(final Activity activity, final List<Post> posts, final Reddit reddit, final int width) {
        this.posts = posts;

        delegatesManager = new AdapterDelegatesManager<>();

        delegatesManager
                .addDelegate(new PostAdapterDelegate(this, activity, reddit, width))
                .addDelegate(new ProgressAdapterDelegate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        delegatesManager.onBindViewHolder(posts, position, holder);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return delegatesManager.getItemViewType(posts, position);
    }
}
