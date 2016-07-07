package com.veyndan.redditclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.common.collect.ImmutableList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import rawjava.Reddit;
import rawjava.model.Image;
import rawjava.model.Link;
import rawjava.model.PostHint;
import rawjava.model.Source;
import rawjava.model.Thing;
import rawjava.network.VoteDirection;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "veyndan_PostAdapter";

    private static final int TYPE_SELF = 0x0;
    private static final int TYPE_IMAGE = 0x1;
    private static final int TYPE_LINK = 0x2;
    private static final int TYPE_LINK_IMAGE = 0x3;

    private static final int TYPE_FLAIR_STICKIED = 0x10;
    private static final int TYPE_FLAIR_NSFW = 0x20;
    private static final int TYPE_FLAIR_LINK = 0x40;
    private static final int TYPE_FLAIR_GILDED = 0x80;

    private static final ImmutableList<String> DIRECT_IMAGE_DOMAINS = ImmutableList.of(
            "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
            "upload.wikimedia.org");

    private final List<Thing<Link>> posts;
    private final Reddit reddit;
    private final int width;

    public PostAdapter(List<Thing<Link>> posts, Reddit reddit, int width) {
        this.posts = posts;
        this.reddit = reddit;
        this.width = width;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.post_item, parent, false);
        ViewStub flairStub = (ViewStub) v.findViewById(R.id.post_flair_stub);
        ViewStub mediaStub = (ViewStub) v.findViewById(R.id.post_media_stub);

        switch (viewType % 16) {
            case TYPE_SELF:
                break;
            case TYPE_IMAGE:
                mediaStub.setLayoutResource(R.layout.post_media_image);
                mediaStub.inflate();
                break;
            case TYPE_LINK:
                mediaStub.setLayoutResource(R.layout.post_media_link);
                mediaStub.inflate();
                break;
            case TYPE_LINK_IMAGE:
                mediaStub.setLayoutResource(R.layout.post_media_link_image);
                mediaStub.inflate();
                break;
            default:
                throw new IllegalStateException("Unknown viewType: " + viewType);
        }

        if ((viewType & (TYPE_FLAIR_STICKIED | TYPE_FLAIR_NSFW | TYPE_FLAIR_LINK | TYPE_FLAIR_GILDED)) != 0) {
            LinearLayout flairContainer = (LinearLayout) flairStub.inflate();

            if ((viewType & TYPE_FLAIR_STICKIED) == TYPE_FLAIR_STICKIED) {
                flairContainer.addView(inflater.inflate(R.layout.post_flair_stickied, flairContainer, false));
            }

            if ((viewType & TYPE_FLAIR_NSFW) == TYPE_FLAIR_NSFW) {
                flairContainer.addView(inflater.inflate(R.layout.post_flair_nsfw, flairContainer, false));
            }

            if ((viewType & TYPE_FLAIR_LINK) == TYPE_FLAIR_LINK) {
                flairContainer.addView(inflater.inflate(R.layout.post_flair_link, flairContainer, false));
            }

            if ((viewType & TYPE_FLAIR_GILDED) == TYPE_FLAIR_GILDED) {
                flairContainer.addView(inflater.inflate(R.layout.post_flair_gilded, flairContainer, false));
            }
        }

        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final Thing<Link> post = posts.get(position);
        final Context context = holder.itemView.getContext();

        holder.title.setText(post.data.title);

        CharSequence age = DateUtils.getRelativeTimeSpanString(
                TimeUnit.SECONDS.toMillis(post.data.createdUtc), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_NOON | DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_NO_MONTH_DAY);

        holder.subtitle.setText(context.getString(R.string.subtitle, post.data.author, age, post.data.subreddit));

        int viewType = holder.getItemViewType();

        switch (viewType % 16) {
            case TYPE_SELF:
                break;
            case TYPE_IMAGE:
                assert holder.mediaContainer != null;
                assert holder.mediaImage != null;
                assert holder.mediaImageProgress != null;

                holder.mediaImageProgress.setVisibility(View.VISIBLE);

                holder.mediaContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Thing<Link> post = posts.get(holder.getAdapterPosition());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.data.url));
                        context.startActivity(intent);
                    }
                });

                final boolean imageDimensAvailable = !post.data.preview.images.isEmpty();

                Glide.with(context)
                        .load(post.data.url)
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
                                    final Image image = new Image();
                                    image.source = new Source();
                                    image.source.width = resource.getIntrinsicWidth();
                                    image.source.height = resource.getIntrinsicHeight();
                                    post.data.preview.images = new ArrayList<>();
                                    post.data.preview.images.add(image);

                                    holder.mediaImage.getLayoutParams().height = (int) ((float) width / image.source.width * image.source.height);
                                }
                                return false;
                            }
                        })
                        .into(holder.mediaImage);
                if (imageDimensAvailable) {
                    Source source = post.data.preview.images.get(0).source;
                    holder.mediaImage.getLayoutParams().height = (int) ((float) width / source.width * source.height);
                }
                break;
            case TYPE_LINK_IMAGE:
                assert holder.mediaImage != null;
                assert holder.mediaImageProgress != null;

                holder.mediaImageProgress.setVisibility(View.VISIBLE);

                Source source = post.data.preview.images.get(0).source;
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
                assert holder.mediaContainer != null;
                assert holder.mediaUrl != null;

                holder.mediaContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Thing<Link> post = posts.get(holder.getAdapterPosition());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.data.url));
                        context.startActivity(intent);
                    }
                });

                String urlHost;
                try {
                    urlHost = new URL(post.data.url).getHost();
                } catch (MalformedURLException e) {
                    Log.e(TAG, e.getMessage(), e);
                    urlHost = post.data.url;
                }

                holder.mediaUrl.setText(urlHost);
                break;
        }

        if ((viewType & TYPE_FLAIR_STICKIED) == TYPE_FLAIR_STICKIED) {
            assert holder.flairStickied != null;
        }

        if ((viewType & TYPE_FLAIR_NSFW) == TYPE_FLAIR_NSFW) {
            assert holder.flairNsfw != null;
        }

        if ((viewType & TYPE_FLAIR_LINK) == TYPE_FLAIR_LINK) {
            assert holder.flairLink != null;

            holder.flairLink.setText(post.data.linkFlairText);
        }

        if ((viewType & TYPE_FLAIR_GILDED) == TYPE_FLAIR_GILDED) {
            assert holder.flairGilded != null;

            holder.flairGilded.setText(String.valueOf(post.data.gilded));
        }

        final String points = context.getResources().getQuantityString(R.plurals.points, post.data.score, post.data.score);
        final String comments = context.getResources().getQuantityString(R.plurals.comments, post.data.numComments, post.data.numComments);
        holder.score.setText(context.getString(R.string.score, points, comments));

        VoteDirection likes = post.data.getLikes();

        holder.upvote.setChecked(likes.equals(VoteDirection.UPVOTE));
        holder.upvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ensure that downvote and upvote aren't checked at the same time.
                if (isChecked) {
                    holder.downvote.setChecked(false);
                }

                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.setLikes(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE);
                reddit.vote(isChecked ? VoteDirection.UPVOTE : VoteDirection.UNVOTE, post.kind + "_" + post.data.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                post.data.score += isChecked ? 1 : -1;

                final String points = context.getResources().getQuantityString(R.plurals.points, post.data.score, post.data.score);
                final String comments = context.getResources().getQuantityString(R.plurals.comments, post.data.numComments, post.data.numComments);
                holder.score.setText(context.getString(R.string.score, points, comments));
            }
        });

        holder.downvote.setChecked(likes.equals(VoteDirection.DOWNVOTE));
        holder.downvote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Ensure that downvote and upvote aren't checked at the same time.
                if (isChecked) {
                    holder.upvote.setChecked(false);
                }

                Thing<Link> post = posts.get(holder.getAdapterPosition());
                post.data.setLikes(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE);
                reddit.vote(isChecked ? VoteDirection.DOWNVOTE : VoteDirection.UNVOTE, post.kind + "_" + post.data.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                post.data.score += isChecked ? -1 : 1;

                final String points = context.getResources().getQuantityString(R.plurals.points, post.data.score, post.data.score);
                final String comments = context.getResources().getQuantityString(R.plurals.comments, post.data.numComments, post.data.numComments);
                holder.score.setText(context.getString(R.string.score, points, comments));
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

        final PopupMenu otherMenu = new PopupMenu(context, holder.other);
        otherMenu.getMenuInflater().inflate(R.menu.menu_post_other, otherMenu.getMenu());

        holder.other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otherMenu.show();
            }
        });

        otherMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int position = holder.getAdapterPosition();
                final Thing<Link> post = posts.get(position);

                switch (item.getItemId()) {
                    case R.id.action_post_hide:
                        final View.OnClickListener undoClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // If undo pressed, then don't follow through with request to hide
                                // the post.
                                posts.add(position, post);
                                notifyItemInserted(position);
                            }
                        };

                        final Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                // If undo pressed, don't hide post.
                                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                    // Chance to undo post hiding has gone, so follow through with
                                    // hiding network request.
                                    reddit.hide(post.kind + "_" + post.data.id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
                                }
                            }
                        };

                        Snackbar.make(holder.itemView, R.string.notify_post_hidden, Snackbar.LENGTH_LONG)
                                .setAction(R.string.notify_post_hidden_undo, undoClickListener)
                                .setCallback(snackbarCallback)
                                .show();

                        // Hide post from list, but make no network request yet. Outcome of the
                        // user's interaction with the snackbar handling will determine this.
                        posts.remove(position);
                        notifyItemRemoved(position);
                        return true;
                    case R.id.action_post_share:
                        return true;
                    case R.id.action_post_profile:
                        return true;
                    case R.id.action_post_subreddit:
                        return true;
                    case R.id.action_post_browser:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.data.url));
                        context.startActivity(intent);
                        return true;
                    case R.id.action_post_report:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        Thing<Link> post = posts.get(position);

        int viewType;

        if (post.data.url.contains("imgur.com/") && !post.data.url.contains("/a/") && !post.data.url.contains("/gallery/") && !post.data.url.contains("i.imgur.com")) {
            post.data.url = post.data.url.replace("imgur.com", "i.imgur.com");
            if (!post.data.url.endsWith(".gifv")) {
                post.data.url += ".png";
            }
            post.data.postHint = PostHint.IMAGE;
        }

        if (post.data.isSelf) {
            viewType = TYPE_SELF;
        } else if ((post.data.postHint != null && post.data.postHint.equals(PostHint.IMAGE)) || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(post.data.url).host())) {
            viewType = TYPE_IMAGE;
        } else if (!post.data.preview.images.isEmpty()) {
            viewType = TYPE_LINK_IMAGE;
        } else {
            viewType = TYPE_LINK;
        }

        if (post.data.stickied) {
            viewType += TYPE_FLAIR_STICKIED;
        }
        if (post.data.over18) {
            viewType += TYPE_FLAIR_NSFW;
        }
        if (!TextUtils.isEmpty(post.data.linkFlairText)) {
            viewType += TYPE_FLAIR_LINK;
        }
        if (post.data.gilded != 0) {
            viewType += TYPE_FLAIR_GILDED;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_title) TextView title;
        @BindView(R.id.post_subtitle) TextView subtitle;
        @BindView(R.id.post_score) TextView score;
        @BindView(R.id.post_upvote) ToggleButton upvote;
        @BindView(R.id.post_downvote) ToggleButton downvote;
        @BindView(R.id.post_save) ToggleButton save;
        @BindView(R.id.post_other) ImageButton other;

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

        // Flair: Stickied
        @Nullable @BindView(R.id.post_flair_stickied) TextView flairStickied;

        // Flair: NSFW
        @Nullable @BindView(R.id.post_flair_nsfw) TextView flairNsfw;

        // Flair: Link
        @Nullable @BindView(R.id.post_flair_link) TextView flairLink;

        // Flair: Gilded
        @Nullable @BindView(R.id.post_flair_gilded) TextView flairGilded;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
