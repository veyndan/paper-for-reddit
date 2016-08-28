package com.veyndan.redditclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.LineHeightSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxPopupMenu;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;
import com.veyndan.redditclient.post.model.Post;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostAdapter extends ProgressAdapter<PostAdapter.PostViewHolder> {

    private static final int TYPE_SELF = 0;
    private static final int TYPE_IMAGES = 1;
    private static final int TYPE_LINK = 3;
    private static final int TYPE_LINK_IMAGE = 4;
    private static final int TYPE_TWEET = 5;
    private static final int TYPE_TEXT = 6;

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
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View cardView = inflater.inflate(R.layout.post_item_link, parent, false);
        final PostViewHolder holder = new PostViewHolder(cardView);

        ButterKnife.bind(this, parent);

        switch (viewType) {
            case TYPE_SELF:
                break;
            case TYPE_IMAGES:
                holder.container.addView(inflater.inflate(R.layout.post_media_images, holder.container, false), 1);
                break;
            case TYPE_LINK:
                holder.container.addView(inflater.inflate(R.layout.post_media_link, holder.container, false), 1);
                break;
            case TYPE_LINK_IMAGE:
                holder.container.addView(inflater.inflate(R.layout.post_media_link_image, holder.container, false), 1);
                break;
            case TYPE_TWEET:
                holder.container.addView(inflater.inflate(R.layout.post_media_tweet, holder.container, false), 1);
                break;
            case TYPE_TEXT:
                holder.container.addView(inflater.inflate(R.layout.post_media_text, holder.container, false), 1);
                break;
            default:
                throw new IllegalStateException("Unknown viewType: " + viewType);
        }

        return new PostViewHolder(cardView);
    }

    @Override
    protected void onBindContentViewHolder(final PostViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();
        final Post post = posts.get(position);
        final Submission submission = post.submission;

        final CharSequence age = DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(submission.createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);

        final int viewType = holder.getItemViewType();

        final List<Flair> flairs = new ArrayList<>();

        if (submission.stickied) {
            flairs.add(new Flair.Builder(flairStickiedColor)
                    .text(flairStickiedText)
                    .build());
        }

        if (submission instanceof Link && ((Link) submission).over18) {
            flairs.add(new Flair.Builder(flairNsfwColor)
                    .text(flairNsfwText)
                    .build());
        }

        if (submission instanceof Link && !TextUtils.isEmpty(((Link) submission).linkFlairText)) {
            flairs.add(new Flair.Builder(flairLinkColor)
                    .text(((Link) submission).linkFlairText)
                    .build());
        }

        if (submission.gilded != 0) {
            flairs.add(new Flair.Builder(flairGildedColor)
                    .text(String.valueOf(submission.gilded))
                    .icon(flairGildedIcon)
                    .build());
        }

        final String points = submission.scoreHidden
                ? scoreHiddenText
                : context.getResources().getQuantityString(R.plurals.points, submission.score, submission.score);

        holder.setHeader(submission.linkTitle, context.getString(R.string.subtitle, submission.author, age, submission.subreddit), flairs);

        switch (viewType) {
            case TYPE_SELF:
                break;
            case TYPE_IMAGES:
                assert holder.mediaContainer != null;

                final LinearLayout linearLayout = (LinearLayout) holder.mediaContainer;
                linearLayout.removeAllViews();

                final LayoutInflater inflater = LayoutInflater.from(context);

                post.getImageObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(image -> {
                            final ViewGroup mediaContainer = (ViewGroup) inflater.inflate(R.layout.post_media_image, linearLayout, false);
                            final ImageView mediaImage = ButterKnife.findById(mediaContainer, R.id.post_media_image);
                            final ProgressBar mediaImageProgress = ButterKnife.findById(mediaContainer, R.id.post_media_image_progress);

                            linearLayout.addView(mediaContainer);

                            mediaImageProgress.setVisibility(View.VISIBLE);

                            if (customTabsClient != null) {
                                final CustomTabsSession session = customTabsClient.newSession(null);
                                session.mayLaunchUrl(Uri.parse(image.getUrl()), null, null);
                            }

                            RxView.clicks(mediaContainer)
                                    .subscribe(aVoid -> {
                                        customTabsIntent.launchUrl(activity, Uri.parse(image.getUrl()));
                                    });

                            final boolean imageDimensAvailable = image.getWidth() > 0 && image.getHeight() > 0;

                            Glide.with(context)
                                    .load(image.getUrl())
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(final Exception e, final String model, final Target<GlideDrawable> target, final boolean isFirstResource) {
                                            mediaImageProgress.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                                            mediaImageProgress.setVisibility(View.GONE);
                                            if (!imageDimensAvailable) {
                                                final int imageWidth = resource.getIntrinsicWidth();
                                                final int imageHeight = resource.getIntrinsicHeight();

                                                image.setWidth(imageWidth);
                                                image.setHeight(imageHeight);

                                                post.setImageObservable(Observable.just(image));

                                                mediaImage.getLayoutParams().height = (int) ((float) width / imageWidth * imageHeight);
                                            }
                                            return false;
                                        }
                                    })
                                    .into(mediaImage);

                            if (imageDimensAvailable) {
                                mediaImage.getLayoutParams().height = (int) ((float) width / image.getWidth() * image.getHeight());
                            }
                        });
                break;
            case TYPE_TWEET:
                assert holder.mediaContainer != null;

                post.getTweetObservable()
                        .subscribe(tweet -> {
                            ((TweetView) holder.mediaContainer).setTweet(tweet);
                        }, throwable -> {
                            Timber.e(throwable, "Load Tweet failure");
                        });
                break;
            case TYPE_LINK_IMAGE:
                Link link = (Link) submission;

                assert holder.mediaImage != null;
                assert holder.mediaImageProgress != null;

                holder.mediaImageProgress.setVisibility(View.VISIBLE);

                final Source source = link.preview.images.get(0).source;
                Glide.with(context)
                        .load(source.url)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(final Exception e, final String model, final Target<GlideDrawable> target, final boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.mediaImage);
            case TYPE_LINK:
                link = (Link) submission;

                assert holder.mediaContainer != null;
                assert holder.mediaUrl != null;

                if (customTabsClient != null) {
                    final CustomTabsSession session = customTabsClient.newSession(null);

                    session.mayLaunchUrl(Uri.parse(submission.linkUrl), null, null);
                }

                RxView.clicks(holder.mediaContainer)
                        .subscribe(aVoid -> {
                            customTabsIntent.launchUrl(activity, Uri.parse(submission.linkUrl));
                        });

                holder.mediaUrl.setText(link.domain);
                break;
            case TYPE_TEXT:
                final Comment comment = (Comment) submission;

                assert holder.mediaText != null;

                holder.mediaText.setText(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(comment.bodyHtml))));
                holder.mediaText.setMovementMethod(LinkMovementMethod.getInstance());
                break;
        }

        final String comments = context.getResources().getQuantityString(R.plurals.comments, submission instanceof Link ? ((Link) submission).numComments : 0, submission instanceof Link ? ((Link) submission).numComments : 0);
        holder.score.setText(context.getString(R.string.score, points, comments));

        final VoteDirection likes = submission.getLikes();

        holder.upvote.setChecked(likes.equals(VoteDirection.UPVOTE));
        RxView.clicks(holder.upvote)
                .subscribe(aVoid -> {
                    holder.upvote.toggle();
                    final boolean isChecked = holder.upvote.isChecked();

                    // Ensure that downvote and upvote aren't checked at the same time.
                    if (isChecked) {
                        holder.downvote.setChecked(false);
                    }

                    submission.setLikes(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE);
                    if (!submission.archived) {
                        reddit.vote(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE, submission.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        submission.score += isChecked ? 1 : -1;

                        final String points1 = submission.scoreHidden
                                ? scoreHiddenText
                                : context.getResources().getQuantityString(R.plurals.points, submission.score, submission.score);
                        final String comments1 = context.getResources().getQuantityString(R.plurals.comments, submission instanceof Link ? ((Link) submission).numComments : 0, submission instanceof Link ? ((Link) submission).numComments : 0);
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

                    submission.setLikes(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE);
                    if (!submission.archived) {
                        reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, submission.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        submission.score += isChecked ? -1 : 1;

                        final String points1 = submission.scoreHidden
                                ? scoreHiddenText
                                : context.getResources().getQuantityString(R.plurals.points, submission.score, submission.score);
                        final String comments1 = context.getResources().getQuantityString(R.plurals.comments, submission instanceof Link ? ((Link) submission).numComments : 0, submission instanceof Link ? ((Link) submission).numComments : 0);
                        holder.score.setText(context.getString(R.string.score, points1, comments1));
                    }
                });

        holder.save.setChecked(submission.saved);
        RxView.clicks(holder.save)
                .subscribe(aVoid -> {
                    holder.save.toggle();
                    final boolean isChecked = holder.save.isChecked();

                    submission.saved = isChecked;
                    if (isChecked) {
                        reddit.save("", submission.getFullname())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    } else {
                        reddit.unsave(submission.getFullname())
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
                                            reddit.hide(submission.getFullname())
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
                            intent.putExtra(Intent.EXTRA_TEXT, submission.getPermalink());
                            intent.setType("text/plain");
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_profile:
                            intent = new Intent(context.getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("username", submission.author);
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_subreddit:
                            intent = new Intent(context.getApplicationContext(), MainActivity.class);
                            intent.putExtra("subreddit", submission.subreddit);
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_browser:
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(submission.linkUrl));
                            context.startActivity(intent);
                            break;
                        case R.id.action_post_report:
                            break;
                    }
                });
    }

    private CharSequence trimTrailingWhitespace(@NonNull final CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    @Override
    public int getContentItemViewType(final int position) {
        final Post post = posts.get(position);
        final Submission submission = post.submission;

        final int viewType;

        if (submission instanceof Comment) {
            viewType = TYPE_TEXT;
        } else if (submission instanceof Link && ((Link) submission).getPostHint().equals(PostHint.SELF)) {
            viewType = TYPE_SELF;
        } else if (post.getTweetObservable() != null) {
            viewType = TYPE_TWEET;
        } else if (submission instanceof Link && ((Link) submission).getPostHint().equals(PostHint.IMAGE)) {
            viewType = TYPE_IMAGES;
        } else if (submission instanceof Link && !((Link) submission).preview.images.isEmpty()) {
            viewType = TYPE_LINK_IMAGE;
        } else {
            viewType = TYPE_LINK;
        }

        return viewType;
    }

    @Override
    protected int getContentItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final Context context;

        @BindView(R.id.post_container) LinearLayout container;
        @BindView(R.id.post_title) TextView title;

        // Media: Image
        // Media: Link
        // Media: Link Image
        @Nullable @BindView(R.id.post_media_container) View mediaContainer;

        // Media: Link Image
        @Nullable @BindView(R.id.post_media_image) ImageView mediaImage;

        // Media: Link Image
        @Nullable @BindView(R.id.post_media_image_progress) ProgressBar mediaImageProgress;

        // Media: Link
        // Media: Link Image
        @Nullable @BindView(R.id.post_media_url) TextView mediaUrl;

        // Media: Text
        @Nullable @BindView(R.id.post_media_text) TextView mediaText;

        @BindView(R.id.post_score) TextView score;
        @BindView(R.id.post_upvote) CheckableImageButton upvote;
        @BindView(R.id.post_downvote) CheckableImageButton downvote;
        @BindView(R.id.post_save) CheckableImageButton save;
        @BindView(R.id.post_other) ImageButton other;

        @BindDimen(R.dimen.post_title_subtitle_spacing) int titleSubtitleSpacing;
        @BindDimen(R.dimen.post_subtitle_flair_spacing) int subtitleFlairSpacing;

        public PostViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
        }

        void setHeader(final String title, final String subtitle, @NonNull final List<Flair> flairs) {
            final TextAppearanceSpan titleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostTitleTextAppearance);

            final TextAppearanceSpan subtitleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostSubtitleTextAppearance);
            final LineHeightSpan subtitleLineHeightSpan = new LineHeightSpan.WithDensity() {
                @Override
                public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv, final int v, final Paint.FontMetricsInt fm, final TextPaint paint) {
                    fm.ascent -= titleSubtitleSpacing;
                    fm.top -= titleSubtitleSpacing;

                    if (!flairs.isEmpty()) {
                        fm.descent += subtitleFlairSpacing;
                        fm.bottom += subtitleFlairSpacing;
                    }
                }

                @Override
                public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv, final int v, final Paint.FontMetricsInt fm) {
                    chooseHeight(text, start, end, spanstartv, v, fm, null);
                }
            };

            final Spanny spanny = new Spanny(title, titleTextAppearanceSpan)
                    .append("\n")
                    .append(subtitle, subtitleTextAppearanceSpan, subtitleLineHeightSpan);

            if (!flairs.isEmpty()) {
                spanny.append("\n");

                final Spanny flairsSpanny = new Spanny();

                String divider = "";
                for (final Flair flair : flairs) {
                    flairsSpanny.append(divider);
                    if (divider.isEmpty()) {
                        divider = "   "; // TODO Replace with margin left and right of 4dp
                    }

                    flairsSpanny.append(flair.getSpannable(context));
                }

                spanny.append(flairsSpanny, new LineHeightSpan.WithDensity() {
                    @Override
                    public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv, final int v, final Paint.FontMetricsInt fm, final TextPaint paint) {
                        // Reset titleSubtitleSpacing.
                        fm.ascent += titleSubtitleSpacing;
                        fm.top += titleSubtitleSpacing;

                        // Reset subtitleFlairSpacing.
                        fm.descent -= subtitleFlairSpacing;
                        fm.bottom -= subtitleFlairSpacing;
                    }

                    @Override
                    public void chooseHeight(final CharSequence text, final int start, final int end, final int spanstartv, final int v, final Paint.FontMetricsInt fm) {
                        chooseHeight(text, start, end, spanstartv, v, fm, null);
                    }
                });
            }

            this.title.setText(spanny);
        }
    }
}
