package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

// t5_
public class Subreddit implements Created, RedditObject {

    private long created;
    @Json(name = "created_utc") private long createdUtc;
    @Json(name = "banner_img") public String bannerImg;
    @Json(name = "user_sr_theme_enabled") public Boolean userSrThemeEnabled;
    @Json(name = "submit_text_html") public Object submitTextHtml;
    @Json(name = "user_is_banned") public Boolean userIsBanned;
    @Json(name = "wiki_enabled") public Boolean wikiEnabled;
    public String id;
    @Json(name = "submit_text") public String submitText;
    @Json(name = "display_name") public String displayName;
    @Json(name = "header_img") public String headerImg;
    @Json(name = "description_html") public String descriptionHtml;
    public String title;
    @Json(name = "collapse_deleted_comments") public Boolean collapseDeletedComments;
    @Json(name = "over_18") public Boolean over18;
    @Json(name = "public_description_html") public String publicDescriptionHtml;
    @Json(name = "icon_size") public List<Integer> iconSize = new ArrayList<>();
    @Json(name = "suggested_comment_sort") public Object suggestedCommentSort;
    @Json(name = "icon_img") public String iconImg;
    @Json(name = "header_title") public String headerTitle;
    public String description;
    @Json(name = "user_is_muted") public Boolean userIsMuted;
    @Json(name = "submit_link_label") public Object submitLinkLabel;
    @Json(name = "accounts_active") public Object accountsActive;
    @Json(name = "public_traffic") public Boolean publicTraffic;
    @Json(name = "header_size") public List<Integer> headerSize = new ArrayList<>();
    public Integer subscribers;
    @Json(name = "submit_text_label") public Object submitTextLabel;
    public String lang;
    @Json(name = "user_is_moderator") public Boolean userIsModerator;
    @Json(name = "key_color") public String keyColor;
    public String name;
    public String url;
    public Boolean quarantine;
    @Json(name = "hide_ads") public Boolean hideAds;
    @Json(name = "banner_size") public List<Integer> bannerSize = new ArrayList<>();
    @Json(name = "user_is_contributor") public Boolean userIsContributor;
    @Json(name = "public_description") public String publicDescription;
    @Json(name = "comment_score_hide_mins") public Integer commentScoreHideMins;
    @Json(name = "subreddit_type") public String subredditType;
    @Json(name = "submission_type") public String submissionType;
    @Json(name = "user_is_subscriber") public Boolean userIsSubscriber;

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public long getCreatedUtc() {
        return createdUtc;
    }
}
