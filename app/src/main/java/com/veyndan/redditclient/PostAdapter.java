package com.veyndan.redditclient;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rawjava.Reddit;
import rawjava.model.Link;
import rawjava.model.Thing;
import rawjava.network.VoteDirection;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "veyndan_PostAdapter";

    private final List<Thing<Link>> posts;
    private final Reddit reddit;

    public PostAdapter(List<Thing<Link>> posts, Reddit reddit) {
        this.posts = posts;
        this.reddit = reddit;
    }

    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostAdapter.PostViewHolder holder, int position) {
        Thing<Link> post = posts.get(position);

        holder.title.setText(post.data.title);

        String age = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(post.data.createdUtc)) + " hrs ago";
        String urlHost;
        try {
            urlHost = new URL(post.data.url).getHost();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            urlHost = post.data.url;
        }

        holder.subtitle.setText(post.data.author + " · " + age + " · " + post.data.subreddit + " · " + urlHost);

        Boolean likes = post.data.likes;

        holder.upvote.setChecked(likes != null && likes);
        holder.upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ensure that downvote and upvote aren't checked at the same time.
                if (isChecked) {
                    holder.downvote.setChecked(false);
                }

                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.likes = isChecked ? true : null;
                reddit.vote(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE, post.kind + "_" + post.data.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

        holder.downvote.setChecked(likes != null && !likes);
        holder.downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ensure that downvote and upvote aren't checked at the same time.
                if (isChecked) {
                    holder.upvote.setChecked(false);
                }

                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.likes = isChecked ? false : null;
                reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, post.kind + "_" + post.data.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

        holder.save.setChecked(post.data.saved);
        holder.save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.saved = isChecked;
                if (isChecked) {
                    reddit.save("", post.kind + "_" + post.data.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                } else {
                    reddit.unsave(post.kind + "_" + post.data.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView subtitle;
        final ToggleButton upvote;
        final ToggleButton downvote;
        final ToggleButton save;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.post_title);
            subtitle = (TextView) itemView.findViewById(R.id.post_subtitle);
            upvote = (ToggleButton) itemView.findViewById(R.id.post_upvote);
            downvote = (ToggleButton) itemView.findViewById(R.id.post_downvote);
            save = (ToggleButton) itemView.findViewById(R.id.post_save);
        }
    }
}
