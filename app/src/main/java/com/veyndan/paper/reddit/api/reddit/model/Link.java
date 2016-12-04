package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.regex.Pattern;

import io.reactivex.Maybe;
import okhttp3.HttpUrl;

public class Link extends Submission {

    @NonNull private static final ImmutableList<String> DIRECT_IMAGE_DOMAINS = ImmutableList.of(
            "i.imgur.com", "i.redd.it", "i.reddituploads.com", "pbs.twimg.com",
            "upload.wikimedia.org");

    private boolean clicked;
    @Nullable private String domain;
    private boolean hidden;
    private boolean isSelf;
    @Nullable private String linkFlairCssClass;
    @Nullable private String linkFlairText;
    private boolean locked;
    @Nullable private Media media;
    @Nullable private MediaEmbed mediaEmbed;
    private int numComments;
    private boolean over18;
    @Nullable private String permalink;
    @Nullable private String thumbnail;
    @Nullable private Object suggestedSort;
    @Nullable private Media secureMedia;
    @Nullable private Object fromKind;
    @NonNull private final Preview preview = new Preview();
    @Nullable private MediaEmbed secureMediaEmbed;
    @NonNull private PostHint postHint = PostHint.LINK;
    @Nullable private Object from;
    @Nullable private Object fromId;
    private boolean quarantine;
    private boolean visited;
    @NonNull private Thing<Listing> replies = new Thing<>(new Listing());

    @NonNull
    @Override
    public PostHint getPostHint() {
        if (isSelf) {
            postHint = PostHint.SELF;
        } else if (Pattern.compile("(.jpg|.jpeg|.gif|.png)$").matcher(linkUrl).find()
                || DIRECT_IMAGE_DOMAINS.contains(HttpUrl.parse(linkUrl).host())) {
            postHint = PostHint.IMAGE;
        }
        return postHint;
    }

    @Nullable
    @Override
    public Object from() {
        return from;
    }

    @Nullable
    @Override
    public Object fromId() {
        return fromId;
    }

    @Override
    public boolean quarantine() {
        return quarantine;
    }

    @Override
    public boolean visited() {
        return visited;
    }

    @Override
    public boolean isClicked() {
        return clicked;
    }

    @NonNull
    @Override
    public Maybe<String> getDomain() {
        return domain == null ? Maybe.empty() : Maybe.just(domain);
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @NonNull
    @Override
    public Maybe<String> getLinkFlairCssClass() {
        return linkFlairCssClass == null ? Maybe.empty() : Maybe.just(linkFlairCssClass);
    }

    @NonNull
    @Override
    public Maybe<String> getLinkFlairText() {
        return linkFlairText == null ? Maybe.empty() : Maybe.just(linkFlairText);
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @NonNull
    @Override
    public Maybe<Media> getMedia() {
        return media == null ? Maybe.empty() : Maybe.just(media);
    }

    @NonNull
    @Override
    public Maybe<MediaEmbed> getMediaEmbed() {
        return mediaEmbed == null ? Maybe.empty() : Maybe.just(mediaEmbed);
    }

    @NonNull
    @Override
    public Maybe<Integer> getNumComments() {
        return Maybe.just(numComments);
    }

    @Override
    public boolean isOver18() {
        return over18;
    }

    @NonNull
    @Override
    public Maybe<String> getThumbnail() {
        return thumbnail == null ? Maybe.empty() : Maybe.just(thumbnail);
    }

    @NonNull
    @Override
    public Maybe<Object> getSuggestedSort() {
        return suggestedSort == null ? Maybe.empty() : Maybe.just(suggestedSort);
    }

    @NonNull
    @Override
    public Maybe<Media> getSecureMedia() {
        return secureMedia == null ? Maybe.empty() : Maybe.just(secureMedia);
    }

    @Nullable
    @Override
    public Object getFromKind() {
        return fromKind;
    }

    @NonNull
    @Override
    public Preview getPreview() {
        return preview;
    }

    @NonNull
    @Override
    public Maybe<MediaEmbed> getSecureMediaEmbed() {
        return secureMediaEmbed == null ? Maybe.empty() : Maybe.just(secureMediaEmbed);
    }

    @NonNull
    @Override
    public Maybe<String> getParentId() {
        return Maybe.empty();
    }

    @NonNull
    @Override
    public Thing<Listing> getReplies() {
        return replies;
    }

    @Nullable
    @Override
    public String getLinkAuthor() {
        return author;
    }

    @NonNull
    @Override
    public String getPermalink() {
        if (permalink == null) {
            throw new IllegalStateException();
        } else {
            return "https://www.reddit.com" + permalink;
        }
    }

    @Nullable
    @Override
    public String getLinkId() {
        return id;
    }

    @Override
    public int getControversiality() {
        throw new UnsupportedOperationException("Method intention unknown");
    }

    @Override
    public boolean isHideable() {
        return true;
    }
}
