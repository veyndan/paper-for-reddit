package com.veyndan.redditclient.api.reddit.network;

import com.veyndan.redditclient.api.reddit.model.Account2;
import com.veyndan.redditclient.api.reddit.model.CaptchaNew;
import com.veyndan.redditclient.api.reddit.model.Categories;
import com.veyndan.redditclient.api.reddit.model.Karma;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Prefs;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.model.Trophies;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Observable;

public interface Network {

    // ================================
    //             Account
    // ================================

    //   GET /api/v1/me
    Observable<Account2> me();

    //   GET /api/v1/me/karma
    Observable<Thing<List<Karma>>> meKarma();

    //   GET /api/v1/me/prefs
    Observable<Prefs> mePrefs();

    // PATCH /api/v1/me/prefs

    //   GET /api/v1/me/trophies
    Observable<Thing<Trophies>> meTrophies();

    //   GET /prefs/where
    //    → /prefs/friends
    //    → /prefs/blocked
    //    → /api/v1/me/friends
    //    → /api/v1/me/blocked

    // ================================
    //             Captcha
    // ================================

    //   GET /api/needs_captcha

    //  POST /api/new_captcha
    Observable<CaptchaNew> newCaptcha();

    //   GET /captcha/iden
    Observable<ResponseBody> idenCaptcha(String iden);

    // ================================
    //              Flair
    // ================================

    //  POST /api/clearflairtemplates
    //  POST /api/deleteflair
    //  POST /api/deleteflairtemplate
    //  POST /api/flair
    //  POST /api/flairconfig
    //  POST /api/flaircsv
    //   GET /api/flairlist
    //  POST /api/flairselector
    //  POST /api/flairtemplate
    //  POST /api/selectflair
    //  POST /api/setflairenabled

    // ================================
    //           Reddit Gold
    // ================================

    //  POST /api/v1/gold/gild/fullname
    //  POST /api/v1/gold/give/username

    // ================================
    //         Links & Comments
    // ================================

    //  POST /api/comment
    //  POST /api/del
    //  POST /api/editusertext
    //  POST /api/hide
    //   GET /api/info
    //  POST /api/lock
    //  POST /api/marknsfw
    //   GET /api/morechildren
    //  POST /api/report

    //  POST /api/save
    Observable<ResponseBody> save(String category, String id);

    //   GET /api/saved_categories
    Observable<Categories> savedCategories();

    //  POST /api/sendreplies
    //  POST /api/set_contest_mode
    //  POST /api/set_subreddit_sticky
    //  POST /api/set_suggested_sort
    //  POST /api/store_visits
    //  POST /api/submit
    //  POST /api/unhide
    //  POST /api/unlock
    //  POST /api/unmarknsfw

    //  POST /api/unsave
    Observable<ResponseBody> unsave(String id);

    //  POST /api/vote
    Observable<ResponseBody> vote(VoteDirection voteDirection, String id);

    // ================================
    //             Listings
    // ================================

    //   GET /by_id/names
    //   GET [/r/subreddit]/comments/article
    //   GET /duplicates/article
    //   GET [/r/subreddit]/random

    //   GET /r/{subreddit}/{where}
    Observable<Thing<Listing>> subreddit(String subreddit, Sort sort);

    Observable<Thing<Listing>> subreddit(
            String subreddit, Sort sort, Map<String, String> queries);

    // ================================
    //           Live Threads
    // ================================

    //  POST /api/live/create
    //  POST /api/live/thread/accept_contributor_invite
    //  POST /api/live/thread/close_thread
    //  POST /api/live/thread/delete_update
    //  POST /api/live/thread/edit
    //  POST /api/live/thread/invite_contributor
    //  POST /api/live/thread/leave_contributor
    //  POST /api/live/thread/report
    //  POST /api/live/thread/rm_contributor
    //  POST /api/live/thread/rm_contributor_invite
    //  POST /api/live/thread/set_contributor_permissions
    //  POST /api/live/thread/strike_update
    //  POST /api/live/thread/update
    //   GET /live/thread
    //   GET /live/thread/about
    //   GET /live/thread/contributors
    //   GET /live/thread/discussions

    // ================================
    //         Private Messages
    // ================================

    //  POST /api/block
    //  POST /api/collapse_message
    //  POST /api/compose
    //  POST /api/del_msg
    //  POST /api/read_all_messages
    //  POST /api/read_message
    //  POST /api/unblock_subreddit
    //  POST /api/uncollapse_message
    //  POST /api/unread_message

    //   GET /message/{where}
    Observable<Thing<Listing>> message(Message message);

    // ================================
    //               Misc
    // ================================

    //   GET /api/v1/scopes

    // ================================
    //            Moderation
    // ================================

    //   GET [/r/subreddit]/about/log
    //   GET [/r/subreddit]/about/reports
    //   GET [/r/subreddit]/about/spam
    //   GET [/r/subreddit]/about/modqueue
    //   GET [/r/subreddit]/about/unmoderated
    //   GET [/r/subreddit]/about/edited
    //  POST [/r/subreddit]/api/accept_moderator_invite
    //  POST /api/approve
    //  POST /api/distinguish
    //  POST /api/ignore_reports
    //  POST /api/leavecontributor
    //  POST /api/leavemoderator
    //  POST /api/mute_message_author
    //  POST /api/remove
    //  POST /api/unignore_reports
    //  POST /api/unmute_message_author
    //  POST [/r/subreddit]/stylesheet

    // ================================
    //              Multis
    // ================================

    //  POST /api/multi/copy
    //   GET /api/multi/mine
    //  POST /api/multi/rename
    //   GET /api/multi/user/username
    //DELETE /api/multi/multipath
    //DELETE /api/filter/filterpath
    //   GET /api/multi/multipath
    //   GET /api/filter/filterpath
    //  POST /api/multi/multipath
    //  POST /api/filter/filterpath
    //   PUT /api/multi/multipath
    //   PUT /api/filter/filterpath
    //   GET /api/multi/multipath/description
    //   PUT /api/multi/multipath/description
    //DELETE /api/multi/multipath/r/srname
    //DELETE /api/filter/filterpath/r/srname
    //   GET /api/multi/multipath/r/srname
    //   GET /api/multi/filterpath/r/srname
    //   PUT /api/multi/multipath/r/srname
    //   PUT /api/multi/filterpath/r/srname

    // ================================
    //              Search
    // ================================

    //   GET [/r/subreddit]/search

    // ================================
    //            Subreddits
    // ================================

    //   GET [/r/subreddit]/about/{where}
    Observable<Thing<Listing>> aboutSubreddit(String subreddit, AboutSubreddit aboutSubreddit);

    //  POST [/r/subreddit]/api/delete_sr_banner
    //  POST [/r/subreddit]/api/delete_sr_header
    //  POST [/r/subreddit]/api/delete_sr_icon
    //  POST [/r/subreddit]/api/delete_sr_img
    //   GET /api/recommend/sr/srnames
    //  POST /api/search_reddit_names
    //  POST /api/site_admin
    //   GET [/r/subreddit]/api/submit_text
    //  POST [/r/subreddit]/api/subreddit_stylesheet
    //   GET /api/subreddits_by_topic
    //   GET /api/subscribe
    //  POST [/r/subreddit]/api/upload_sr_img
    //   GET /r/subreddit/about
    //   GET /r/subreddit/about/edit
    //   GET [/r/subreddit]/rules
    //   GET [/r/subreddit]/sidebar
    //   GET [/r/subreddit]/sticky

    //   GET /subreddits/mine/{where}
    Observable<Thing<Listing>> mySubreddits(MySubreddits mySubreddits);

    //   GET /subreddits/search

    //   GET /subreddits/{where}
    Observable<Thing<Listing>> subreddits(SubredditSort sort);

    // ================================
    //              Users
    // ================================

    //  POST [/r/subreddit]/api/friend
    //  POST [/r/subreddit]/api/setpermissions
    //  POST [/r/subreddit]/api/unfriend
    //   GET /api/username_available
    //DELETE /api/v1/me/friends/username
    //   GET /api/v1/me/friends/username
    //   PUT /api/v1/me/friends/username
    //   GET /api/v1/user/username/trophies

    //   GET /user/username/about

    //   GET /user/{username}/{where}
    Observable<Thing<Listing>> user(String username, User where);

    // ================================
    //              Wiki
    // ================================

    //  POST [/r/subreddit]/api/wiki/alloweditor/add
    //  POST [/r/subreddit]/api/wiki/alloweditor/del
    //  POST [/r/subreddit]/api/wiki/edit
    //  POST [/r/subreddit]/api/wiki/hide
    //  POST [/r/subreddit]/api/wiki/revert
    //   GET [/r/subreddit]/wiki/discussions/page
    //   GET [/r/subreddit]/wiki/pages
    //   GET [/r/subreddit]/wiki/revisions
    //   GET [/r/subreddit]/wiki/revisions/page
    //   GET [/r/subreddit]/wiki/settings/page
    //  POST [/r/subreddit]/wiki/settings/page
    //   GET [/r/subreddit]/wiki/page
}
