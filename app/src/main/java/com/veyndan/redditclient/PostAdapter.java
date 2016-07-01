package com.veyndan.redditclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import rawjava.Reddit;
import rawjava.model.Link;
import rawjava.model.Thing;
import rawjava.network.VoteDirection;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

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
        holder.title.setText(posts.get(position).data.title);

        Boolean likes = posts.get(holder.getAdapterPosition()).data.likes;

        holder.upvote.setChecked(likes != null && likes);
        holder.upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.likes = isChecked ? false : null;
                reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, post.kind + "_" + post.data.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final ToggleButton upvote;
        final ToggleButton downvote;

        public PostViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.post_title);
            upvote = (ToggleButton) itemView.findViewById(R.id.post_upvote);
            downvote = (ToggleButton) itemView.findViewById(R.id.post_downvote);
        }
    }
}
