package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

// t5_
@AutoValue
public abstract class Subreddit implements Created, RedditObject {

    @Json(name = "banner_img")
    public abstract String bannerImg();

    @Json(name = "user_sr_theme_enabled")
    public abstract Boolean userSrThemeEnabled();

    @Json(name = "submit_text_html")
    public abstract Object submitTextHtml();

    @Json(name = "user_is_banned")
    public abstract Boolean userIsBanned();

    @Json(name = "wiki_enabled")
    public abstract Boolean wikiEnabled();

    public abstract String id();

    @Json(name = "submit_text")
    public abstract String submitText();

    @Json(name = "display_name")
    public abstract String displayName();

    @Json(name = "header_img")
    public abstract String headerImg();

    @Json(name = "description_html")
    public abstract String descriptionHtml();

    public abstract String title();

    @Json(name = "collapse_deleted_comments")
    public abstract Boolean collapseDeletedComments();

    @Json(name = "over_18")
    public abstract Boolean over18();

    @Json(name = "public_description_html")
    public abstract String publicDescriptionHtml();

    @Json(name = "icon_size")
    public abstract List<Integer> iconSize();

    @Json(name = "suggested_comment_sort")
    public abstract Object suggestedCommentSort();

    @Json(name = "icon_img")
    public abstract String iconImg();

    @Json(name = "header_title")
    public abstract String headerTitle();

    public abstract String description();

    @Json(name = "user_is_muted")
    public abstract Boolean userIsMuted();

    @Json(name = "submit_link_label")
    public abstract Object submitLinkLabel();

    @Json(name = "accounts_active")
    public abstract Object accountsActive();

    @Json(name = "public_traffic")
    public abstract Boolean publicTraffic();

    @Json(name = "header_size")
    public abstract List<Integer> headerSize();

    public abstract Integer subscribers();

    @Json(name = "submit_text_label")
    public abstract Object submitTextLabel();

    public abstract String lang();

    @Json(name = "user_is_moderator")
    public abstract Boolean userIsModerator();

    @Json(name = "key_color")
    public abstract String keyColor();

    public abstract String name();

    public abstract String url();

    public abstract Boolean quarantine();

    @Json(name = "hide_ads")
    public abstract Boolean hideAds();

    @Json(name = "banner_size")
    public abstract List<Integer> bannerSize();

    @Json(name = "user_is_contributor")
    public abstract Boolean userIsContributor();

    @Json(name = "public_description")
    public abstract String publicDescription();

    @Json(name = "comment_score_hide_mins")
    public abstract Integer commentScoreHideMins();

    @Json(name = "subreddit_type")
    public abstract String subredditType();

    @Json(name = "submission_type")
    public abstract String submissionType();

    @Json(name = "user_is_subscriber")
    public abstract Boolean userIsSubscriber();

    public static JsonAdapter<Subreddit> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Subreddit.MoshiJsonAdapter(moshi);
    }
}
