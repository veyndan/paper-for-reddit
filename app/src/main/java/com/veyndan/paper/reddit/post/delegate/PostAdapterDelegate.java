package com.veyndan.paper.reddit.post.delegate;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxPopupMenu;
import com.veyndan.paper.reddit.Filter;
import com.veyndan.paper.reddit.MainActivity;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;
import com.veyndan.paper.reddit.post.Flair;
import com.veyndan.paper.reddit.post.PostAdapter;
import com.veyndan.paper.reddit.post.media.PostMediaAdapter;
import com.veyndan.paper.reddit.post.model.Post;
import com.veyndan.paper.reddit.ui.recyclerview.Swipeable;
import com.veyndan.paper.reddit.ui.widget.PostHeaderView;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.schedulers.Schedulers;

public class PostAdapterDelegate extends AdapterDelegate<List<Node<Response<Thing<Listing>>>>> {

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    @BindColor(R.color.post_flair_stickied) int flairStickiedColor;
    @BindColor(R.color.post_flair_nsfw) int flairNsfwColor;
    @BindColor(R.color.post_flair_link) int flairLinkColor;
    @BindColor(R.color.post_flair_gilded) int flairGildedColor;

    @BindDrawable(R.drawable.ic_star_white_12sp) Drawable flairGildedIcon;

    @BindString(R.string.post_stickied) String flairStickiedText;
    @BindString(R.string.post_nsfw) String flairNsfwText;
    @BindString(R.string.score_hidden) String scoreHiddenText;

    private final PostAdapter adapter;
    private final Activity activity;
    private final Reddit reddit;

    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    private final CustomTabsIntent customTabsIntent = builder.build();
    @Nullable private CustomTabsClient customTabsClient;

    public PostAdapterDelegate(final PostAdapter adapter, final Activity activity, final Reddit reddit) {
        this.adapter = adapter;
        this.activity = activity;
        this.reddit = reddit;

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
    public boolean isForViewType(@NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                 final int position) {
        return nodes.get(position) instanceof Post;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
        ButterKnife.bind(this, parent);
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View postView = inflater.inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(postView, adapter, reddit);
    }

    @Override
    protected void onBindViewHolder(@NonNull final List<Node<Response<Thing<Listing>>>> nodes,
                                    final int position,
                                    @NonNull final RecyclerView.ViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        final PostViewHolder postHolder = (PostViewHolder) holder;
        final Post post = (Post) nodes.get(position);

        bindHeader(post, postHolder);
        bindMedia(post, postHolder);
        bindPoints(context, post, postHolder);
        bindUpvoteAction(context, post, postHolder);
        bindDownvoteAction(context, post, postHolder);
        bindSaveAction(post, postHolder);
        bindCommentsAction(context, nodes, post, postHolder);

        final PopupMenu otherMenu = new PopupMenu(context, postHolder.other);
        otherMenu.getMenuInflater().inflate(R.menu.menu_post_other, otherMenu.getMenu());

        RxView.clicks(postHolder.other)
                .subscribe(aVoid -> otherMenu.show());

        RxPopupMenu.itemClicks(otherMenu)
                .subscribe(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_post_share:
                            bindShareAction(context, post);
                            break;
                        case R.id.action_post_browser:
                            bindBrowserAction(context, post);
                            break;
                        case R.id.action_post_report:
                            break;
                    }
                });
    }

    private void bindHeader(final Post post, final PostViewHolder holder) {
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

        holder.header.setHeader(post.getLinkTitle(), post.getAuthor(), post.getDisplayAge(),
                post.getSubreddit(), flairs);
    }

    private void bindMedia(final Post post, final PostViewHolder holder) {
        final PostMediaAdapter postMediaAdapter = new PostMediaAdapter(
                activity, customTabsClient, customTabsIntent, post, post.getMedias());
        holder.mediaView.setAdapter(postMediaAdapter);
    }

    private void bindPoints(final Context context, final Post post, final PostViewHolder holder) {
        final String points = post.getDisplayPoints(context, scoreHiddenText);
        holder.score.setText(points);
    }

    private void bindUpvoteAction(final Context context, final Post post, final PostViewHolder holder) {
        final VoteDirection likes = post.getLikes();
        holder.upvote.setChecked(likes == VoteDirection.UPVOTE);
        RxCompoundButton.checkedChanges(holder.upvote)
                // checkedChanges emits the checked state on subscription. As the voted state of
                // the Reddit post is the same as the checked state of the button initially,
                // skipping the initial emission means no unnecessary network requests occur.
                .skip(1)
                .subscribe(isChecked -> {
                    post.setLikes(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE);
                    if (!post.isArchived()) {
                        reddit.vote(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE, post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .subscribe();

                        post.setPoints(post.getPoints() + (isChecked ? 1 : -1));

                        final String points1 = post.getDisplayPoints(context, scoreHiddenText);
                        holder.score.setText(points1);
                    }
                });
    }

    private void bindDownvoteAction(final Context context, final Post post, final PostViewHolder holder) {
        final VoteDirection likes = post.getLikes();
        holder.downvote.setChecked(likes == VoteDirection.DOWNVOTE);
        RxCompoundButton.checkedChanges(holder.downvote)
                // checkedChanges emits the checked state on subscription. As the voted state of
                // the Reddit post is the same as the checked state of the button initially,
                // skipping the initial emission means no unnecessary network requests occur.
                .skip(1)
                .subscribe(isChecked -> {
                    post.setLikes(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE);
                    if (!post.isArchived()) {
                        reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .subscribe();

                        post.setPoints(post.getPoints() + (isChecked ? -1 : 1));

                        final String points1 = post.getDisplayPoints(context, scoreHiddenText);
                        holder.score.setText(points1);
                    }
                });
    }

    private void bindSaveAction(final Post post, final PostViewHolder holder) {
        holder.save.setChecked(post.isSaved());
        RxView.clicks(holder.save)
                .subscribe(aVoid -> {
                    holder.save.toggle();
                    final boolean isChecked = holder.save.isChecked();

                    post.setSaved(isChecked);
                    if (isChecked) {
                        reddit.save("", post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    } else {
                        reddit.unsave(post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    }
                });
    }

    private void bindCommentsAction(final Context context, final List<Node<Response<Thing<Listing>>>> nodes, final Post post, final PostViewHolder holder) {
        RxView.clicks(holder.comments)
                .map(aVoid -> {
                    holder.comments.toggle();
                    return holder.comments.isChecked();
                })
                .subscribe(displayDescendants -> {
                    post.setDescendantsVisible(!displayDescendants);
                    if (post.isComment()) {
                        if (displayDescendants) {
                            int i;
                            for (i = holder.getAdapterPosition() + 1; i < nodes.size() && nodes.get(i).getDepth() > post.getDepth(); i++)
                                ;

                            nodes.subList(holder.getAdapterPosition() + 1, i).clear();
                            adapter.notifyItemRangeRemoved(holder.getAdapterPosition() + 1, i - (holder.getAdapterPosition() + 1));

                            holder.commentCount.setVisibility(View.VISIBLE);
                            holder.commentCount.setText(String.valueOf(i - (holder.getAdapterPosition() + 1)));
                        } else {
                            post.preOrderTraverse(post.getDepth())
                                    .skip(1)
                                    .toList()
                                    .subscribe(children -> {
                                        nodes.addAll(holder.getAdapterPosition() + 1, children);
                                        adapter.notifyItemRangeInserted(holder.getAdapterPosition() + 1, children.size());
                                    });

                            holder.commentCount.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        final Intent commentsIntent = new Intent(context, MainActivity.class);
                        commentsIntent.putExtra(Filter.COMMENTS_SUBREDDIT, post.getSubreddit());
                        commentsIntent.putExtra(Filter.COMMENTS_ARTICLE, post.getArticle());
                        context.startActivity(commentsIntent);
                    }
                });

        if (post.isInternalNode() && !post.isDescendantsVisible()) {
            holder.commentCount.setVisibility(View.VISIBLE);
            final String commentCount = post.getDisplayDescendants();
            holder.commentCount.setText(commentCount);
        } else {
            holder.commentCount.setVisibility(View.INVISIBLE);
        }
    }

    private static void bindShareAction(final Context context, final Post post) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, post.getPermalink());
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    private static void bindBrowserAction(final Context context, final Post post) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getLinkUrl()));
        context.startActivity(intent);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder implements Swipeable {

        @BindView(R.id.post_header) PostHeaderView header;
        @BindView(R.id.post_media_view) RecyclerView mediaView;
        @BindView(R.id.post_score) TextView score;
        @BindView(R.id.post_vote) RadioGroup voteGroup;
        @BindView(R.id.post_upvote_new) RadioButton upvote;
        @BindView(R.id.post_downvote_new) RadioButton downvote;
        @BindView(R.id.post_save) CheckableImageButton save;
        @BindView(R.id.post_comments) CheckableImageButton comments;
        @BindView(R.id.post_comment_count) TextView commentCount;
        @BindView(R.id.post_other) ImageButton other;

        private final PostAdapter adapter;
        private final Reddit reddit;

        PostViewHolder(final View itemView, final PostAdapter adapter, final Reddit reddit) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.adapter = adapter;
            this.reddit = reddit;
        }

        @Override
        public boolean swipeable() {
            final int position = getAdapterPosition();
            final Post post = (Post) adapter.getItems().get(position);
            return post.isHideable();
        }

        @Override
        public void onSwipe() {
            final int position = getAdapterPosition();
            final Node<Response<Thing<Listing>>> node = adapter.getItems().get(position);
            final Post post = (Post) node;

            final View.OnClickListener undoClickListener = view -> {
                // If undo pressed, then don't follow through with request to hide
                // the post.
                adapter.getItems().add(position, node);
                adapter.notifyItemInserted(position);
            };

            final Snackbar snackbar = Snackbar.make(itemView, R.string.notify_post_hidden, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_post_hidden_undo, undoClickListener);

            RxSnackbar.dismisses(snackbar)
                    // If undo pressed, don't hide post.
                    .takeFirst(event -> event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                    .subscribe(event -> {
                        // Chance to undo post hiding has gone, so follow through with
                        // hiding network request.
                        reddit.hide(post.getFullname())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    });

            snackbar.show();

            // Hide post from list, but make no network request yet. Outcome of the
            // user's interaction with the snackbar handling will determine this.
            adapter.getItems().remove(position);
            adapter.notifyItemRemoved(position);
        }
    }
}
