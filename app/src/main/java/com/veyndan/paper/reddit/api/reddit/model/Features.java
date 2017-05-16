package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Features {

    @Json(name = "inline_image_previews_logged_out")
    public abstract boolean inlineImagePreviewsLoggedOut();

    @Json(name = "relevancy_M_web")
    public abstract Experiment relevancyMweb();

    @Json(name = "relevancy_sidebar")
    public abstract Experiment relevancySidebar();

    @Json(name = "live_happening_now")
    public abstract boolean liveHappeningNow();

    @Json(name = "adserver_reporting")
    public abstract boolean adserverReporting();

    @Json(name = "inline_image_previews_logged_in")
    public abstract boolean inlineImagePreviewsLoggedIn();

    @Json(name = "legacy_search_pref")
    public abstract boolean legacySearchPref();

    @Json(name = "mobile_web_targeting")
    public abstract boolean mobileWebTargeting();

    @Json(name = "adzerk_do_not_track")
    public abstract boolean adzerkDoNotTrack();

    @Json(name = "sticky_comments")
    public abstract boolean stickyComments();

    @Json(name = "upgrade_cookies")
    public abstract boolean upgradeCookies();

    @Json(name = "ads_auto_refund")
    public abstract boolean adsAutoRefund();

    @Json(name = "ads_auction")
    public abstract boolean adsAuction();

    @Json(name = "imgur_gif_conversion")
    public abstract boolean imgurGifConversion();

    @Json(name = "image_scraper")
    public abstract boolean imageScraper();

    @Json(name = "expando_events")
    public abstract boolean expandoEvents();

    @Json(name = "eu_cookie_policy")
    public abstract boolean euCookiePolicy();

    @Json(name = "force_https")
    public abstract boolean forceHttps();

    @Json(name = "mobile_native_banner")
    public abstract boolean mobileNativeBanner();

    @Json(name = "do_not_track")
    public abstract boolean doNotTrack();

    @Json(name = "stylesheets_everywhere")
    public abstract boolean stylesheetsEverywhere();

    @Json(name = "new_loggedin_cache_policy")
    public abstract boolean newLoggedinCachePolicy();

    @Json(name = "registration_captcha")
    public abstract Experiment registrationCaptcha();

    @Json(name = "https_redirect")
    public abstract boolean httpsRedirect();

    @Json(name = "screenview_events")
    public abstract boolean screenviewEvents();

    @Json(name = "pause_ads")
    public abstract boolean pauseAds();

    @Json(name = "give_hsts_grants")
    public abstract boolean giveHstsGrants();

    @Json(name = "new_report_dialog")
    public abstract boolean newReportDialog();

    @Json(name = "moat_tracking")
    public abstract boolean moatTracking();

    @Json(name = "subreddit_rules")
    public abstract boolean subredditRules();

    public abstract boolean timeouts();

    @Json(name = "mobile_settings")
    public abstract boolean mobileSettings();

    @Json(name = "youtube_scraper")
    public abstract boolean youtubeScraper();

    @Json(name = "activity_service_write")
    public abstract boolean activityServiceWrite();

    @Json(name = "ads_auto_extend")
    public abstract boolean adsAutoExtend();

    @Json(name = "post_embed")
    public abstract boolean postEmbed();

    @Json(name = "autoexpand_media_subreddit_setting")
    public abstract boolean autoexpandMediaSubredditSetting();

    @Json(name = "adblock_test")
    public abstract boolean adblockTest();

    @Json(name = "activity_service_read")
    public abstract boolean activityServiceRead();

    public static JsonAdapter<Features> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Features.MoshiJsonAdapter(moshi);
    }
}
