package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

import java.util.Collections;

import io.reactivex.Observable;

public class Prefs {
    @Json(name = "default_theme_sr") public String defaultThemeSr;
    @Json(name = "threaded_messages") public Boolean threadedMessages;
    @Json(name = "hide_downs") public Boolean hideDowns;
    @Json(name = "show_stylesheets") public Boolean showStylesheets;
    @Json(name = "show_link_flair") public Boolean showLinkFlair;
    @Json(name = "creddit_autorenew") public Boolean credditAutorenew;
    @Json(name = "show_trending") public Boolean showTrending;
    @Json(name = "private_feeds") public Boolean privateFeeds;
    @Json(name = "monitor_mentions") public Boolean monitorMentions;
    @Json(name = "show_snoovatar") public Boolean showSnoovatar;
    public Boolean research;
    @Json(name = "ignore_suggested_sort") public Boolean ignoreSuggestedSort;
    @Json(name = "num_comments") public Integer numComments;
    public Boolean clickgadget;
    @Json(name = "use_global_defaults") public Boolean useGlobalDefaults;
    @Json(name = "label_nsfw") public Boolean labelNsfw;
    @Json(name = "affiliate_links") public Boolean affiliateLinks;
    @Json(name = "over_18") public Boolean over18;
    @Json(name = "email_messages") public Boolean emailMessages;
    @Json(name = "highlight_controversial") public Boolean highlightControversial;
    @Json(name = "no_profanity") public Boolean noProfanity;
    @Json(name = "domain_details") public Boolean domainDetails;
    @Json(name = "collapse_left_bar") public Boolean collapseLeftBar;
    public String lang;
    @Json(name = "hide_ups") public Boolean hideUps;
    @Json(name = "public_server_seconds") public Boolean publicServerSeconds;
    @Json(name = "hide_from_robots") public Boolean hideFromRobots;
    public Boolean compress;
    @Json(name = "store_visits") public Boolean storeVisits;
    @Json(name = "threaded_modmail") public Boolean threadedModmail;
    @Json(name = "min_link_score") public Integer minLinkScore;
    @Json(name = "media_preview") public String mediaPreview;
    @Json(name = "enable_default_themes") public Boolean enableDefaultThemes;
    @Json(name = "content_langs") private final Iterable<String> contentLangs = Collections.emptyList();
    @Json(name = "show_promote") public Object showPromote;
    @Json(name = "min_comment_score") public Integer minCommentScore;
    @Json(name = "public_votes") public Boolean publicVotes;
    public Boolean organic;
    @Json(name = "collapse_read_messages") public Boolean collapseReadMessages;
    @Json(name = "show_flair") public Boolean showFlair;
    @Json(name = "mark_messages_read") public Boolean markMessagesRead;
    @Json(name = "force_https") public Boolean forceHttps;
    @Json(name = "hide_ads") public Boolean hideAds;
    public Boolean beta;
    public Boolean newwindow;
    public Integer numsites;
    @Json(name = "legacy_search") public Boolean legacySearch;
    public String media;
    @Json(name = "show_gold_expiration") public Boolean showGoldExpiration;
    @Json(name = "highlight_new_comments") public Boolean highlightNewComments;
    @Json(name = "default_comment_sort") public String defaultCommentSort;
    @Json(name = "hide_locationbar") public Boolean hideLocationbar;

    public Observable<String> getContentLangs() {
        return Observable.fromIterable(contentLangs);
    }
}
