package com.veyndan.paper.reddit.post.delegate.post;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.databinding.ItemPostHeaderBinding;
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.ButterKnife;
import retrofit2.Response;

public class PostHeaderAdapterDelegate extends AdapterDelegate<List<Node<Response<Thing<Listing>>>>> {

    @BindColor(R.color.post_flair_locked) int flairLockedColor;
    @BindColor(R.color.post_flair_stickied) int flairStickiedColor;
    @BindColor(R.color.post_flair_nsfw) int flairNsfwColor;
    @BindColor(R.color.post_flair_link) int flairLinkColor;
    @BindColor(R.color.post_flair_gilded) int flairGildedColor;

    @BindDrawable(R.drawable.ic_lock_outline_white_12sp) Drawable flairLockIcon;
    @BindDrawable(R.drawable.ic_star_white_12sp) Drawable flairGildedIcon;

    @BindString(R.string.post_locked) String flairLockedText;
    @BindString(R.string.post_stickied) String flairStickiedText;
    @BindString(R.string.post_nsfw) String flairNsfwText;

    @Override
    protected boolean isForViewType(@NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position) {
        return position % 2 == 0 && nodes.get(position) instanceof Post;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        ButterKnife.bind(this, parent);
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemPostHeaderBinding binding = ItemPostHeaderBinding.inflate(inflater, parent, false);
        return new PostHeaderViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position,
                                    @NonNull final RecyclerView.ViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        bindHeader((Post) nodes.get(position), (PostHeaderViewHolder) holder);
    }

    private void bindHeader(final Post post, final PostHeaderViewHolder holder) {
        final List<Flair> flairs = new ArrayList<>();

        if (post.isStickied()) {
            flairs.add(new Flair.Builder(flairStickiedColor)
                    .text(flairStickiedText)
                    .build());
        }

        if (post.isLocked()) {
            flairs.add(new Flair.Builder(flairLockedColor)
                    .text(flairLockedText)
                    .icon(flairLockIcon)
                    .build());
        }

        if (post.isNsfw()) {
            flairs.add(new Flair.Builder(flairNsfwColor)
                    .text(flairNsfwText)
                    .build());
        }

        if (post.hasLinkFlair()) {
            flairs.add(new Flair.Builder(flairLinkColor)
                    .type(Flair.Type.LINK)
                    .text(post.getLinkFlair())
                    .build());
        }

        if (post.isGilded()) {
            flairs.add(new Flair.Builder(flairGildedColor)
                    .text(String.valueOf(post.getGildedCount()))
                    .icon(flairGildedIcon)
                    .build());
        }

        holder.binding.postHeader.setHeader(post.getLinkTitle(), post.getAuthor(), post.getDisplayAge(),
                post.getSubreddit(), flairs);
    }

    private static final class PostHeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemPostHeaderBinding binding;

        private PostHeaderViewHolder(final ItemPostHeaderBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
