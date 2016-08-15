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
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ToggleButton;

import com.binaryfork.spanny.Spanny;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.support.design.widget.RxSnackbar;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxPopupMenu;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.veyndan.redditclient.api.imgur.model.Image;
import com.veyndan.redditclient.api.imgur.network.ImgurService;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.api.reddit.model.Submission;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostAdapter extends ProgressAdapter<PostAdapter.PostViewHolder> {

    private static final int TYPE_SELF = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ALBUM = 2;
    private static final int TYPE_LINK = 3;
    private static final int TYPE_LINK_IMAGE = 4;
    private static final int TYPE_TWEET = 5;
    private static final int TYPE_TEXT = 6;

    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    private final Activity activity;
    private final List<RedditObject> posts;
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

    public PostAdapter(Activity activity, List<RedditObject> posts, Reddit reddit, int width) {
        this.activity = activity;
        this.posts = posts;
        this.reddit = reddit;
        this.width = width;

        CustomTabsClient.bindCustomTabsService(activity, CUSTOM_TAB_PACKAGE_NAME, new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                // customTabsClient is now valid.
                customTabsClient = client;
                customTabsClient.warmup(0);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // customTabsClient is no longer valid. This also invalidates sessions.
                customTabsClient = null;
            }
        });
    }

    @Override
    protected PostViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View cardView = inflater.inflate(R.layout.post_item_link, parent, false);
        final PostViewHolder holder = new PostViewHolder(cardView);

        ButterKnife.bind(this, parent);

        switch (viewType) {
            case TYPE_SELF:
                break;
            case TYPE_IMAGE:
                holder.container.addView(inflater.inflate(R.layout.post_media_image, holder.container, false), 1);
                break;
            case TYPE_ALBUM:
                holder.container.addView(inflater.inflate(R.layout.post_media_album, holder.container, false), 1);
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
    protected void onBindContentViewHolder(PostViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final Submission submission = (Submission) posts.get(position);

        CharSequence age = DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(submission.createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);

        int viewType = holder.getItemViewType();

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
                Link link = (Link) posts.get(position);

                break;
            case TYPE_IMAGE:
                final Link finalLink = (Link) posts.get(position);

                assert holder.mediaContainer != null;
                assert holder.mediaImage != null;
                assert holder.mediaImageProgress != null;

                holder.mediaImageProgress.setVisibility(View.VISIBLE);

                if (customTabsClient != null) {
                    CustomTabsSession session = customTabsClient.newSession(new CustomTabsCallback());
                    session.mayLaunchUrl(Uri.parse(submission.linkUrl), null, null);
                }

                RxView.clicks(holder.mediaContainer)
                        .subscribe(aVoid -> {
                            customTabsIntent.launchUrl(activity, Uri.parse(submission.linkUrl));
                        });

                final boolean imageDimensAvailable = !finalLink.preview.images.isEmpty();

                Glide.with(context)
                        .load(submission.linkUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                if (!imageDimensAvailable) {
                                    final com.veyndan.redditclient.api.reddit.model.Image image = new com.veyndan.redditclient.api.reddit.model.Image();
                                    image.source.width = resource.getIntrinsicWidth();
                                    image.source.height = resource.getIntrinsicHeight();
                                    finalLink.preview.images.add(image);

                                    holder.mediaImage.getLayoutParams().height = (int) ((float) width / image.source.width * image.source.height);
                                }
                                return false;
                            }
                        })
                        .into(holder.mediaImage);
                if (imageDimensAvailable) {
                    Source source = finalLink.preview.images.get(0).source;
                    holder.mediaImage.getLayoutParams().height = (int) ((float) width / source.width * source.height);
                }
                break;
            case TYPE_ALBUM:
                link = (Link) posts.get(position);

                assert holder.mediaContainer != null;

                RecyclerView recyclerView = (RecyclerView) holder.mediaContainer;

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                final List<Image> images = new ArrayList<>();

                final AlbumAdapter albumAdapter = new AlbumAdapter(activity, images, width, customTabsClient, customTabsIntent);
                recyclerView.setAdapter(albumAdapter);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request request = chain.request().newBuilder()
                                    .addHeader("Authorization", "Client-ID " + Config.IMGUR_CLIENT_ID)
                                    .build();
                            return chain.proceed(request);
                        })
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.imgur.com/3/")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                ImgurService imgurService = retrofit.create(ImgurService.class);

                imgurService.album(submission.linkUrl.split("/a/")[1])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(basic -> {
                            images.addAll(basic.data.images);
                            albumAdapter.notifyDataSetChanged();
                        });
                break;
            case TYPE_TWEET:
                link = (Link) posts.get(position);

                assert holder.mediaContainer != null;

                final Pattern pattern = Pattern.compile("https://twitter.com/\\w*/status/(\\d+)$");
                final Matcher matcher = pattern.matcher(submission.linkUrl);

                if (matcher.find()) {
                    final Optional<Long> tweetId = UrlMatcher.Twitter.tweetId(submission.linkUrl);

                    if (tweetId.isPresent()) {
                        TweetUtils.loadTweet(tweetId.get(), new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                ((TweetView) holder.mediaContainer).setTweet(result.data);
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Timber.e(exception, "Load Tweet failure");
                            }
                        });
                    } else {
                        // TODO Show default link view as tweetId couldn't be parsed.
                    }
                }
                break;
            case TYPE_LINK_IMAGE:
                link = (Link) posts.get(position);

                assert holder.mediaImage != null;
                assert holder.mediaImageProgress != null;

                holder.mediaImageProgress.setVisibility(View.VISIBLE);

                Source source = link.preview.images.get(0).source;
                Glide.with(context)
                        .load(source.url)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.mediaImageProgress.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.mediaImage);
            case TYPE_LINK:
                link = (Link) posts.get(position);

                assert holder.mediaContainer != null;
                assert holder.mediaUrl != null;

                if (customTabsClient != null) {
                    CustomTabsSession session = customTabsClient.newSession(null);

                    session.mayLaunchUrl(Uri.parse(submission.linkUrl), null, null);
                }

                RxView.clicks(holder.mediaContainer)
                        .subscribe(aVoid -> {
                            customTabsIntent.launchUrl(activity, Uri.parse(submission.linkUrl));
                        });

                holder.mediaUrl.setText(link.domain);
                break;
            case TYPE_TEXT:
                final Comment comment = (Comment) posts.get(position);

                assert holder.mediaText != null;

                holder.mediaText.setText(trimTrailingWhitespace(Html.fromHtml(StringEscapeUtils.unescapeHtml4(comment.bodyHtml))));
                holder.mediaText.setMovementMethod(LinkMovementMethod.getInstance());
                break;
        }

        final String comments = context.getResources().getQuantityString(R.plurals.comments, submission instanceof Link ? ((Link) submission).numComments : 0, submission instanceof Link ? ((Link) submission).numComments : 0);
        holder.score.setText(context.getString(R.string.score, points, comments));

        VoteDirection likes = submission.getLikes();

        holder.upvote.setChecked(likes.equals(VoteDirection.UPVOTE));
        RxCompoundButton.checkedChanges(holder.upvote)
                .skip(1)
                .subscribe(isChecked -> {
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
        RxCompoundButton.checkedChanges(holder.downvote)
                .skip(1)
                .subscribe(isChecked -> {
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
        RxCompoundButton.checkedChanges(holder.save)
                .skip(1)
                .subscribe(isChecked -> {
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
                                posts.add(adapterPosition, submission);
                                notifyItemInserted(adapterPosition);
                            };

                            Snackbar snackbar = Snackbar.make(holder.itemView, R.string.notify_post_hidden, Snackbar.LENGTH_LONG)
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

    private CharSequence trimTrailingWhitespace(@NonNull CharSequence source) {
        int i = source.length();

        // loop back to the first non-whitespace character
        do {
            i--;
        } while (i > 0 && Character.isWhitespace(source.charAt(i)));

        return source.subSequence(0, i + 1);
    }

    @Override
    public int getContentItemViewType(int position) {
        Submission submission = (Submission) posts.get(position);

        int viewType;

        if (submission instanceof Link && submission.linkUrl.contains("imgur.com/") && !submission.linkUrl.contains("/a/") && !submission.linkUrl.contains("/gallery/") && !submission.linkUrl.contains("i.imgur.com")) {
            submission.linkUrl = submission.linkUrl.replace("imgur.com", "i.imgur.com");
            if (!submission.linkUrl.endsWith(".gifv")) {
                submission.linkUrl += ".png";
            }
            ((Link) submission).setPostHint(PostHint.IMAGE);
        }

        if (submission instanceof Comment) {
            viewType = TYPE_TEXT;
        } else if (submission instanceof Link && ((Link) submission).getPostHint().equals(PostHint.SELF)) {
            viewType = TYPE_SELF;
        } else if (submission instanceof Link && UrlMatcher.Twitter.tweetId(submission.linkUrl).isPresent()) {
            viewType = TYPE_TWEET;
        } else if (submission instanceof Link && ((Link) submission).getPostHint().equals(PostHint.IMAGE)) {
            viewType = TYPE_IMAGE;
        } else if (submission instanceof Link && submission.linkUrl.contains("/a/")) {
            viewType = TYPE_ALBUM;
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

        // Media: Image
        // Media: Link Image
        @Nullable @BindView(R.id.post_media_image) ImageView mediaImage;

        // Media: Image
        // Media: Link Image
        @Nullable @BindView(R.id.post_media_image_progress) ProgressBar mediaImageProgress;

        // Media: Link
        // Media: Link Image
        @Nullable @BindView(R.id.post_media_url) TextView mediaUrl;

        // Media: Text
        @Nullable @BindView(R.id.post_media_text) TextView mediaText;

        @BindView(R.id.post_score) TextView score;
        @BindView(R.id.post_upvote) ToggleButton upvote;
        @BindView(R.id.post_downvote) ToggleButton downvote;
        @BindView(R.id.post_save) ToggleButton save;
        @BindView(R.id.post_other) ImageButton other;

        @BindDimen(R.dimen.post_title_subtitle_spacing) int titleSubtitleSpacing;
        @BindDimen(R.dimen.post_subtitle_flair_spacing) int subtitleFlairSpacing;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            context = itemView.getContext();
        }

        void setHeader(final String title, final String subtitle, @NonNull final List<Flair> flairs) {
            final TextAppearanceSpan titleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostTitleTextAppearance);

            final TextAppearanceSpan subtitleTextAppearanceSpan = new TextAppearanceSpan(context, R.style.PostSubtitleTextAppearance);
            final LineHeightSpan subtitleLineHeightSpan = new LineHeightSpan.WithDensity() {
                @Override
                public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
                    fm.ascent -= titleSubtitleSpacing;
                    fm.top -= titleSubtitleSpacing;

                    if (!flairs.isEmpty()) {
                        fm.descent += subtitleFlairSpacing;
                        fm.bottom += subtitleFlairSpacing;
                    }
                }

                @Override
                public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
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
                for (Flair flair : flairs) {
                    flairsSpanny.append(divider);
                    if (divider.isEmpty()) {
                        divider = "   "; // TODO Replace with margin left and right of 4dp
                    }

                    flairsSpanny.append(flair.getSpannable(context));
                }

                spanny.append(flairsSpanny, new LineHeightSpan.WithDensity() {
                    @Override
                    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
                        // Reset titleSubtitleSpacing.
                        fm.ascent += titleSubtitleSpacing;
                        fm.top += titleSubtitleSpacing;

                        // Reset subtitleFlairSpacing.
                        fm.descent -= subtitleFlairSpacing;
                        fm.bottom -= subtitleFlairSpacing;
                    }

                    @Override
                    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
                        chooseHeight(text, start, end, spanstartv, v, fm, null);
                    }
                });
            }

            this.title.setText(spanny);
        }
    }
}
