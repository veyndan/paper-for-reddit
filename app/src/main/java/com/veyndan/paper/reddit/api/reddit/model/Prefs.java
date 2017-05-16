package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Prefs {

    @Json(name = "default_theme_sr")
    public abstract String defaultThemeSr();

    @Json(name = "threaded_messages")
    public abstract Boolean threadedMessages();

    @Json(name = "hide_downs")
    public abstract Boolean hideDowns();

    @Json(name = "show_stylesheets")
    public abstract Boolean showStylesheets();

    @Json(name = "show_link_flair")
    public abstract Boolean showLinkFlair();

    @Json(name = "creddit_autorenew")
    public abstract Boolean credditAutorenew();

    @Json(name = "show_trending")
    public abstract Boolean showTrending();

    @Json(name = "private_feeds")
    public abstract Boolean privateFeeds();

    @Json(name = "monitor_mentions")
    public abstract Boolean monitorMentions();

    @Json(name = "show_snoovatar")
    public abstract Boolean showSnoovatar();

    public abstract Boolean research();

    @Json(name = "ignore_suggested_sort")
    public abstract Boolean ignoreSuggestedSort();

    @Json(name = "num_comments")
    public abstract Integer numComments();

    public abstract Boolean clickgadget();

    @Json(name = "use_global_defaults")
    public abstract Boolean useGlobalDefaults();

    @Json(name = "label_nsfw")
    public abstract Boolean labelNsfw();

    @Json(name = "affiliate_links")
    public abstract Boolean affiliateLinks();

    @Json(name = "over_18")
    public abstract Boolean over18();

    @Json(name = "email_messages")
    public abstract Boolean emailMessages();

    @Json(name = "highlight_controversial")
    public abstract Boolean highlightControversial();

    @Json(name = "no_profanity")
    public abstract Boolean noProfanity();

    @Json(name = "domain_details")
    public abstract Boolean domainDetails();

    @Json(name = "collapse_left_bar")
    public abstract Boolean collapseLeftBar();

    public abstract String lang();

    @Json(name = "hide_ups")
    public abstract Boolean hideUps();

    @Json(name = "public_server_seconds")
    public abstract Boolean publicServerSeconds();

    @Json(name = "hide_from_robots")
    public abstract Boolean hideFromRobots();

    public abstract Boolean compress();

    @Json(name = "store_visits")
    public abstract Boolean storeVisits();

    @Json(name = "threaded_modmail")
    public abstract Boolean threadedModmail();

    @Json(name = "min_link_score")
    public abstract Integer minLinkScore();

    @Json(name = "media_preview")
    public abstract String mediaPreview();

    @Json(name = "enable_default_themes")
    public abstract Boolean enableDefaultThemes();

    @Json(name = "content_langs")
    public abstract List<String> contentLangs();

    @Json(name = "show_promote")
    public abstract Object showPromote();

    @Json(name = "min_comment_score")
    public abstract Integer minCommentScore();

    @Json(name = "public_votes")
    public abstract Boolean publicVotes();

    public abstract Boolean organic();

    @Json(name = "collapse_read_messages")
    public abstract Boolean collapseReadMessages();

    @Json(name = "show_flair")
    public abstract Boolean showFlair();

    @Json(name = "mark_messages_read")
    public abstract Boolean markMessagesRead();

    @Json(name = "force_https")
    public abstract Boolean forceHttps();

    @Json(name = "hide_ads")
    public abstract Boolean hideAds();

    public abstract Boolean beta();

    public abstract Boolean newwindow();

    public abstract Integer numsites();

    @Json(name = "legacy_search")
    public abstract Boolean legacySearch();

    public abstract String media();

    @Json(name = "show_gold_expiration")
    public abstract Boolean showGoldExpiration();

    @Json(name = "highlight_new_comments")
    public abstract Boolean highlightNewComments();

    @Json(name = "default_comment_sort")
    public abstract String defaultCommentSort();

    @Json(name = "hide_locationbar")
    public abstract Boolean hideLocationbar();

    public static JsonAdapter<Prefs> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Prefs.MoshiJsonAdapter(moshi);
    }
}
