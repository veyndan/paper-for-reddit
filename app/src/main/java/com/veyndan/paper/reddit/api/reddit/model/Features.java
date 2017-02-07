package com.veyndan.paper.reddit.api.reddit.model;

import com.squareup.moshi.Json;

public class Features {
    @Json(name = "inline_image_previews_logged_out") public boolean inlineImagePreviewsLoggedOut;
    @Json(name = "relevancy_M_web") public Experiment relevancyMweb;
    @Json(name = "relevancy_sidebar") public Experiment relevancySidebar;
    @Json(name = "live_happening_now") public boolean liveHappeningNow;
    @Json(name = "adserver_reporting") public boolean adserverReporting;
    @Json(name = "inline_image_previews_logged_in") public boolean inlineImagePreviewsLoggedIn;
    @Json(name = "legacy_search_pref") public boolean legacySearchPref;
    @Json(name = "mobile_web_targeting") public boolean mobileWebTargeting;
    @Json(name = "adzerk_do_not_track") public boolean adzerkDoNotTrack;
    @Json(name = "sticky_comments") public boolean stickyComments;
    @Json(name = "upgrade_cookies") public boolean upgradeCookies;
    @Json(name = "ads_auto_refund") public boolean adsAutoRefund;
    @Json(name = "ads_auction") public boolean adsAuction;
    @Json(name = "imgur_gif_conversion") public boolean imgurGifConversion;
    @Json(name = "image_scraper") public boolean imageScraper;
    @Json(name = "expando_events") public boolean expandoEvents;
    @Json(name = "eu_cookie_policy") public boolean euCookiePolicy;
    @Json(name = "force_https") public boolean forceHttps;
    @Json(name = "mobile_native_banner") public boolean mobileNativeBanner;
    @Json(name = "do_not_track") public boolean doNotTrack;
    @Json(name = "stylesheets_everywhere") public boolean stylesheetsEverywhere;
    @Json(name = "new_loggedin_cache_policy") public boolean newLoggedinCachePolicy;
    @Json(name = "registration_captcha") public Experiment registrationCaptcha;
    @Json(name = "https_redirect") public boolean httpsRedirect;
    @Json(name = "screenview_events") public boolean screenviewEvents;
    @Json(name = "pause_ads") public boolean pauseAds;
    @Json(name = "give_hsts_grants") public boolean giveHstsGrants;
    @Json(name = "new_report_dialog") public boolean newReportDialog;
    @Json(name = "moat_tracking") public boolean moatTracking;
    @Json(name = "subreddit_rules") public boolean subredditRules;
    public boolean timeouts;
    @Json(name = "mobile_settings") public boolean mobileSettings;
    @Json(name = "youtube_scraper") public boolean youtubeScraper;
    @Json(name = "activity_service_write") public boolean activityServiceWrite;
    @Json(name = "ads_auto_extend") public boolean adsAutoExtend;
    @Json(name = "post_embed") public boolean postEmbed;
    @Json(name = "autoexpand_media_subreddit_setting") public boolean autoexpandMediaSubredditSetting;
    @Json(name = "adblock_test") public boolean adblockTest;
    @Json(name = "activity_service_read") public boolean activityServiceRead;
}
