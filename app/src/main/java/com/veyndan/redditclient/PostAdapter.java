package com.veyndan.redditclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxPopupMenu;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;
import com.veyndan.redditclient.post.PostMediaAdapter;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.ui.widget.PostHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostAdapter extends ProgressAdapter<PostAdapter.PostViewHolder> {

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    private final Activity activity;
    private final List<Post> posts;
    private final Reddit reddit;
    private final int width;

    @BindColor(R.color.post_flair_stickied) int flairStickiedColor;
    @BindColor(R.color.post_flair_nsfw) int flairNsfwColor;
    @BindColor(R.color.post_flair_link) int flairLinkColor;
    @BindColor(R.color.post_flair_gilded) int flairGildedColor;

    @BindDrawable(R.drawable.ic_star_white_12sp) Drawable flairGildedIcon;

    @BindString(R.string.post_stickied) String flairStickiedText;
    @BindString(R.string.post_nsfw) String flairNsfwText;
    @BindString(R.string.score_hidden) String scoreHiddenText;

    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    private final CustomTabsIntent customTabsIntent = builder.build();
    @Nullable private CustomTabsClient customTabsClient;

    public PostAdapter(final Activity activity, final List<Post> posts, final Reddit reddit, final int width) {
        this.activity = activity;
        this.posts = posts;
        this.reddit = reddit;
        this.width = width;

        CustomTabsClient.bindCustomTabsService(activity, CUSTOM_TAB_PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(final ComponentName name, final CustomTabsClient client) {
                // customTabsClient is now valid.
                customTabsClient = client;
                customTabsClient.warmup(0);
            }

            @Override
            public void onServiceDisconnected(final ComponentName name) {
                // customTabsClient is no longer valid. This also invalidates sessions.
                customTabsClient = null;
            }
        });
    }

    @Override
    protected PostViewHolder onCreateContentViewHolder(final ViewGroup parent, final int viewType) {
        ButterKnife.bind(this, parent);
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View cardView = inflater.inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(cardView);
    }

    @Override
    protected void onBindContentViewHolder(final PostViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();
        final Post post = posts.get(position);

        final List<Flair> flairs = new ArrayList<>();

        if (post.isStickied()) {
            flairs.add(new Flair.Builder(flairStickiedColor)
                    .text(flairStickiedText)
                    .build());
        }

        if (post.isNsfw()) {
            flairs.add(new Flair.Builder(flairNsfwColor)
                    .text(flairNsfwText)
                    .build());
        }

        if (post.hasLinkFlair()) {
            flairs.add(new Flair.Builder(flairLinkColor)
                    .text(post.getLinkFlair())
                    .build());
        }

        if (post.isGilded()) {
            flairs.add(new Flair.Builder(flairGildedColor)
                    .text(String.valueOf(post.getGildedCount()))
                    .icon(flairGildedIcon)
                    .build());
        }

        final String subtitle = context.getString(R.string.subtitle, post.getAuthor(),
                post.getDisplayAge(), post.getSubreddit());
        holder.header.setHeader(post.getLinkTitle(), subtitle, flairs);

        final List<Object> items = new ArrayList<>();

        final PostMediaAdapter postMediaAdapter = new PostMediaAdapter(
                activity, customTabsClient, customTabsIntent, post, width, items);
        holder.mediaView.setAdapter(postMediaAdapter);

        post.getMediaObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    items.add(item);
                    postMediaAdapter.notifyDataSetChanged();
                }, Timber::e);

        final String points = post.getDisplayPoints(context, scoreHiddenText);
        final String comments = post.getDisplayComments(context);
        holder.score.setText(context.getString(R.string.score, points, comments));

        final VoteDirection likes = post.getLikes();

        holder.upvote.setChecked(likes.equals(VoteDirection.UPVOTE));
        RxView.clicks(holder.upvote)
                .subscribe(aVoid -> {
                    holder.upvote.toggle();
                    final boolean isChecked = holder.upvote.isChecked();

                    // Ensure that downvote and upvote aren't checked at the same time.
                    if (isChecked) {
                        holder.downvote.setChecked(false);
                    }

                    post.setLikes(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE);
                    if (!post.isArchived()) {
                        reddit.vote(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE, post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        post.setPoints(post.getPoints() + (isChecked ? 1 : -1));

                        final String points1 = post.getDisplayPoints(context, scoreHiddenText);
                        final String comments1 = post.getDisplayComments(context);
                        holder.score.setText(context.getString(R.string.score, points1, comments1));
                    }
                });

        holder.downvote.setChecked(likes.equals(VoteDirection.DOWNVOTE));
        RxView.clicks(holder.downvote)
                .subscribe(aVoid -> {
                    holder.downvote.toggle();
                    final boolean isChecked = holder.downvote.isChecked();

                    // Ensure that downvote and upvote aren't checked at the same time.
                    if (isChecked) {
                        holder.upvote.setChecked(false);
                    }

                    post.setLikes(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE);
                    if (!post.isArchived()) {
                        reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        post.setPoints(post.getPoints() + (isChecked ? -1 : 1));

                        final String points1 = post.getDisplayPoints(context, scoreHiddenText);
                        final String comments1 = post.getDisplayComments(context);
                        holder.score.setText(context.getString(R.string.score, points1, comments1));
                    }
                });

        holder.save.setChecked(post.isSaved());
        RxView.clicks(holder.save)
                .subscribe(aVoid -> {
                    holder.save.toggle();
                    final boolean isChecked = holder.save.isChecked();

                    post.setSaved(isChecked);
                    if (isChecked) {
                        reddit.save("", post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    } else {
                        reddit.unsave(post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    }
                });

        final PopupMenu otherMenu = new PopupMenu(context, holder.other);
        otherMenu.getMenuInflater().inflate(R.menu.menu_post_other, otherMenu.getMenu());

        RxView.clicks(holder.other)
                .subscribe(aVoid -> otherMenu.show());

        RxPopupMenu.itemClicks(otherMenu)
                .subscribe(menuItem -> {
                    final int adapterPosition = holder.getAdapterPosition();

                    switch (menuItem.getItemId()) {
                        case R.id.action_post_hide:
                            final View.OnClickListener undoClickListener = view -> {
                                // If undo pressed, then don't follow through with request to hide
                                // the post.
                                posts.add(adapterPosition, post);
                                notifyItemInserted(adapterPosition);
                            };

                            final Snackbar snackbar = Snackbar.make(holder.itemView, R.string.notify_post_hidden, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.notify_post_hidden_undo, undoClickListener);

                            RxSnackbar.dismisses(snackbar)
                                    .subscribe(event -> {
                                        // If undo pressed, don't hide post.
                                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                            // Chance to undo post hiding has gone, so follow through with
                                            // hiding network request.
                                            reddit.hide(post.getFullname())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe();
                                        }
                                    });

                            snackbar.show();

                            // Hide post from list, but make no network request yet. Outcome of the
                            // user's interaction with the snackbar handling will determine this.
                            posts.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            break;
                        case R.id.action_post_share:
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, post.getPermalink());
                            intent.setType("text/plain");
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_profile:
                            intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("username", post.getAuthor());
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_subreddit:
                            intent = new Intent(context.getApplicationContext(), MainActivity.class);
                            intent.putExtra("subreddit", post.getSubreddit());
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_browser:
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLinkUrl()));
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_report:
                            break;
                    }
                });
    }

    @Override
    public int getContentItemViewType(final int position) {
        return 0;
    }

    @Override
    protected int getContentItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_header) PostHeaderView header;
        @BindView(R.id.post_media_view) RecyclerView mediaView;
        @BindView(R.id.post_score) TextView score;
        @BindView(R.id.post_upvote) CheckableImageButton upvote;
        @BindView(R.id.post_downvote) CheckableImageButton downvote;
        @BindView(R.id.post_save) CheckableImageButton save;
        @BindView(R.id.post_other) ImageButton other;

        PostViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
