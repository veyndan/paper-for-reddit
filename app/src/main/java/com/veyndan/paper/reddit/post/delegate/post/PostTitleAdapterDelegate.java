package com.veyndan.paper.reddit.post.delegate.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.ui.widget.PostHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;

public class PostTitleAdapterDelegate extends AbsListItemAdapterDelegate<Post.Title, Object, PostTitleAdapterDelegate.TitleViewHolder> {

    @BindColor(R.color.post_flair_stickied) int flairStickiedColor;
    @BindColor(R.color.post_flair_nsfw) int flairNsfwColor;
    @BindColor(R.color.post_flair_link) int flairLinkColor;
    @BindColor(R.color.post_flair_gilded) int flairGildedColor;

    @BindDrawable(R.drawable.ic_star_white_12sp) Drawable flairGildedIcon;

    @BindString(R.string.post_stickied) String flairStickiedText;
    @BindString(R.string.post_nsfw) String flairNsfwText;

    @Override
    protected boolean isForViewType(@NonNull final Object particle,
                                    @NonNull final List<Object> particles,
                                    final int position) {
        return particle instanceof Post.Title;
    }

    @NonNull
    @Override
    protected TitleViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        ButterKnife.bind(this, parent);
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View titleView = inflater.inflate(R.layout.item_post_title, parent, false);
        return new TitleViewHolder(titleView);
    }

    @Override
    protected void onBindViewHolder(@NonNull final Post.Title title,
                                    @NonNull final TitleViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final List<Flair> flairs = new ArrayList<>();

        if (title.isStickied()) {
            flairs.add(new Flair.Builder(flairStickiedColor)
                    .text(flairStickiedText)
                    .build());
        }

        if (title.isNsfw()) {
            flairs.add(new Flair.Builder(flairNsfwColor)
                    .text(flairNsfwText)
                    .build());
        }

        if (title.hasLinkFlair()) {
            flairs.add(new Flair.Builder(flairLinkColor)
                    .text(title.getLinkFlair())
                    .build());
        }

        if (title.isGilded()) {
            flairs.add(new Flair.Builder(flairGildedColor)
                    .text(String.valueOf(title.getGildedCount()))
                    .icon(flairGildedIcon)
                    .build());
        }

        holder.header.setHeader(title.getLinkTitle(), title.getAuthor(), title.getDisplayAge(),
                title.getSubreddit(), flairs);
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        private final PostHeaderView header;

        TitleViewHolder(final View itemView) {
            super(itemView);

            header = (PostHeaderView) itemView;
        }
    }
}
