package com.veyndan.paper.reddit.api.reddit;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.veyndan.paper.reddit.api.reddit.json.adapter.DefaultOnDataMismatchAdapter;
import com.veyndan.paper.reddit.api.reddit.json.adapter.RedditObjectAdapter;
import com.veyndan.paper.reddit.api.reddit.model.Account2;
import com.veyndan.paper.reddit.api.reddit.model.CaptchaNew;
import com.veyndan.paper.reddit.api.reddit.model.Categories;
import com.veyndan.paper.reddit.api.reddit.model.Karma;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.MoreChildren;
import com.veyndan.paper.reddit.api.reddit.model.Prefs;
import com.veyndan.paper.reddit.api.reddit.model.Submission;
import com.veyndan.paper.reddit.api.reddit.model.Subreddit;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.model.Trophies;
import com.veyndan.paper.reddit.api.reddit.network.AboutSubreddit;
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService;
import com.veyndan.paper.reddit.api.reddit.network.Credentials;
import com.veyndan.paper.reddit.api.reddit.network.Message;
import com.veyndan.paper.reddit.api.reddit.network.MySubreddits;
import com.veyndan.paper.reddit.api.reddit.network.RedditService;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.api.reddit.network.SubredditSort;
import com.veyndan.paper.reddit.api.reddit.network.TimePeriod;
import com.veyndan.paper.reddit.api.reddit.network.User;
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.AccessTokenInterceptor;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.AuthorizationInterceptor;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.RawJsonInterceptor;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.UserAgentInterceptor;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class Reddit {

    private static final String FILTER_NODE_DEPTH = "node_depth";

    private static final String FILTER_COMMENTS_SUBREDDIT = "comments_subreddit";
    private static final String FILTER_COMMENTS_ARTICLE = "comments_article";

    private static final String FILTER_TIME_PERIOD = "time_period";

    public static final String FILTER_SUBREDDIT_NAME = "subreddit_name";

    private static final String FILTER_SEARCH_QUERY = "search_subreddit";

    private static final String FILTER_USER_NAME = "user_name";
    private static final String FILTER_USER_COMMENTS = "user_comments";
    private static final String FILTER_USER_SUBMITTED = "user_submitted";
    private static final String FILTER_USER_GILDED = "user_gilded";

    private final RedditService redditService;

    public Reddit(final Credentials credentials) {
        final Moshi moshi = new Moshi.Builder()
                .add(RedditObjectAdapter.FACTORY)
                // According to the Reddit json contract, if there are no replies an empty string
                // is returned instead of an empty object or null as expected. This sets an empty
                // object if there are no replies.
                .add(DefaultOnDataMismatchAdapter.newFactory(Types.newParameterizedType(Thing.class, Listing.class), new Thing<>(new Listing())))
                .build();

        final MoshiConverterFactory jsonConverterFactory = MoshiConverterFactory.create(moshi);

        final RxJava2CallAdapterFactory rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();

        final OkHttpClient client = new OkHttpClient();

        final OkHttpClient.Builder authenticationClientBuilder = client.newBuilder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AuthorizationInterceptor(credentials));

        final Retrofit authenticatorRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(authenticationClientBuilder.build())
                .build();

        final AuthenticationService authenticationService = authenticatorRetrofit.create(AuthenticationService.class);

        final OkHttpClient.Builder clientBuilder = client.newBuilder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AccessTokenInterceptor(authenticationService, credentials))
                .addInterceptor(new RawJsonInterceptor());

        if (Config.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor());
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://oauth.reddit.com/")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(clientBuilder.build())
                .build();

        redditService = retrofit.create(RedditService.class);
    }

    private static HttpLoggingInterceptor loggingInterceptor() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        return loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    // ================================
    //             Account
    // ================================

    public Single<Response<Account2>> me() {
        return redditService.me();
    }

    public Single<Response<Thing<List<Karma>>>> myKarma() {
        return redditService.myKarma();
    }

    public Single<Response<Prefs>> myPrefs() {
        return redditService.myPrefs();
    }

    public Single<Response<Thing<Trophies>>> myTrophies() {
        return redditService.myTrophies();
    }

    // ================================
    //             Captcha
    // ================================

    public Single<Response<CaptchaNew>> newCaptcha() {
        return redditService.newCaptcha("json");
    }

    public Single<Response<Void>> idenCaptcha(final String iden) {
        return redditService.idenCaptcha(iden);
    }

    // ================================
    //         Links & Comments
    // ================================

    public Single<Response<Void>> hide(final String... ids) {
        return hide(Arrays.asList(ids));
    }

    public Single<Response<Void>> hide(final List<String> ids) {
        return redditService.hide(TextUtils.join(",", ids));
    }

    public Single<MoreChildren> moreChildren(final List<String> children, final String linkId) {
        return redditService.moreChildren("json", TextUtils.join(",", children), linkId);
    }

    public Single<Response<Void>> save(final String category, final String id) {
        return redditService.save(category, id);
    }

    public Single<Response<Categories>> savedCategories() {
        return redditService.savedCategories();
    }

    public Single<Response<Void>> unsave(final String id) {
        return redditService.unsave(id);
    }

    public Single<Response<Void>> vote(final VoteDirection voteDirection, final String id) {
        return redditService.vote(voteDirection, id);
    }

    // ================================
    //             Listings
    // ================================

    private Single<Response<Thing<Listing>>> subredditComments(final String subreddit, final String article) {
        return redditService.subredditComments(subreddit, article)
                .map(response -> {
                    final List<Thing<Listing>> things = response.body();
                    ((Submission) things.get(0).data.children.get(0)).getReplies().data.children.addAll(things.get(1).data.children);
                    return Response.success(things.get(0));
                });
    }

    private Single<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort,
                                                       @Nullable final TimePeriod timePeriod) {
        final QueryBuilder query = new QueryBuilder();

        if (timePeriod != null) {
            query.t(timePeriod);
        }

        final Single<Response<Thing<Listing>>> request = redditService.subreddit(subreddit, sort, query.build());
        return paginate(request, query);
    }

    // ================================
    //         Private Messages
    // ================================

    public Single<Response<Thing<Listing>>> message(final Message message) {
        return redditService.message(message);
    }

    // ================================
    //              Search
    // ================================

    private Single<Response<Thing<Listing>>> search(final String subreddit, final String searchQuery) {
        final QueryBuilder query = new QueryBuilder()
                .q(searchQuery);

        return redditService.search(subreddit, query.build());
    }

    // ================================
    //            Subreddits
    // ================================

    public Single<Response<Thing<Listing>>> mySubreddits(final MySubreddits mySubreddits) {
        return redditService.mySubreddits(mySubreddits);
    }

    public Single<Response<Thing<Listing>>> subreddits(final SubredditSort sort) {
        return redditService.subreddits(sort);
    }

    public Single<Response<Thing<Subreddit>>> subredditAbout(final String subreddit) {
        return redditService.subredditAbout(subreddit);
    }

    // ================================
    //              Users
    // ================================

    public Single<Response<Thing<Listing>>> aboutSubreddit(
            final String subreddit, final AboutSubreddit where) {
        return redditService.aboutSubreddit(subreddit, where);
    }

    public Single<Response<Thing<Trophies>>> userTrophies(final String username) {
        return redditService.userTrophies(username);
    }

    public Single<Response<Thing<Account2>>> userAbout(final String username) {
        return redditService.userAbout(username);
    }

    private Single<Response<Thing<Listing>>> user(final String username, final User where,
                                                  @Nullable final TimePeriod timePeriod) {
        final QueryBuilder query = new QueryBuilder();

        if (timePeriod != null) {
            query.t(timePeriod);
        }

        final Single<Response<Thing<Listing>>> request = redditService.user(username, where, query.build());
        return paginate(request, query);
    }

    public Single<Response<Thing<Listing>>> query(final Bundle params, final Sort sort) {
        final int nodeDepth = params.getInt(FILTER_NODE_DEPTH, -1);

        final String commentsSubreddit = params.getString(FILTER_COMMENTS_SUBREDDIT, "");
        final String commentsArticle = params.getString(FILTER_COMMENTS_ARTICLE, "");

        final TimePeriod timePeriod = (TimePeriod) params.getSerializable(FILTER_TIME_PERIOD);

        final String subredditName = params.getString(FILTER_SUBREDDIT_NAME, "");

        final String searchQuery = params.getString(FILTER_SEARCH_QUERY, "");

        final String userName = params.getString(FILTER_USER_NAME, "");
        final boolean userComments = params.getBoolean(FILTER_USER_COMMENTS, false);
        final boolean userSubmitted = params.getBoolean(FILTER_USER_SUBMITTED, false);
        final boolean userGilded = params.getBoolean(FILTER_USER_GILDED, false);

        if (nodeDepth == -1) {
            // TODO Get every post, along with its comments here i.e. the whole forest of Reddit
            throw new IllegalStateException();
        } else if (nodeDepth == 0) {
            if (commentsSubreddit.isEmpty() && commentsArticle.isEmpty()
                    && searchQuery.isEmpty()
                    && userName.isEmpty() && !userComments && !userSubmitted && !userGilded) {
                return subreddit(MoreObjects.firstNonNull(subredditName, "all"), sort, MoreObjects.firstNonNull(timePeriod, TimePeriod.ALL));
            }

            if (commentsSubreddit.isEmpty() && commentsArticle.isEmpty()
                    && subredditName.isEmpty()
                    && searchQuery.isEmpty()
                    && userName.length() > 1) {
                final User userWhere = toUser(userComments, userSubmitted, userGilded);
                return user(userName, userWhere, timePeriod);
            }

            if (commentsSubreddit.isEmpty() && commentsArticle.isEmpty()
                    && timePeriod == null
                    && subredditName.length() > 0
                    && searchQuery.length() > 0
                    && userName.isEmpty() && !userComments && !userSubmitted && !userGilded) {
                return search(subredditName, searchQuery);
            }

            if (commentsSubreddit.length() > 1 && commentsArticle.length() > 1
                    && timePeriod == null
                    && subredditName.isEmpty()
                    && searchQuery.isEmpty()
                    && userName.isEmpty() && !userComments && !userSubmitted && !userGilded) {
                return subredditComments(commentsSubreddit, commentsArticle);
            }

            throw new IllegalStateException();
        } else {
            throw new IllegalStateException("Depths that aren't 0 or -1 aren't available to the Reddit API");
        }
    }

    private static Single<Response<Thing<Listing>>> paginate(final Single<Response<Thing<Listing>>> page, final QueryBuilder query) {
        return Single.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query1 -> !query1.build().containsKey("after") || query1.build().get("after") != null)
                .flatMapSingle(query1 -> page)
                .doOnSuccess(response -> query.after(response.body().data.after));
    }

    public static class FilterBuilder {

        private final Bundle bundle = new Bundle();

        public FilterBuilder nodeDepth(@IntRange(from = 0) final int nodeDepth) {
            bundle.putInt(FILTER_NODE_DEPTH, nodeDepth);
            return this;
        }

        public FilterBuilder commentsSubreddit(final String commentsSubreddit) {
            bundle.putString(FILTER_COMMENTS_SUBREDDIT, commentsSubreddit);
            return this;
        }

        public FilterBuilder commentsArticle(final String commentsArticle) {
            bundle.putString(FILTER_COMMENTS_ARTICLE, commentsArticle);
            return this;
        }

        public FilterBuilder timePeriod(final TimePeriod timePeriod) {
            bundle.putSerializable(FILTER_TIME_PERIOD, timePeriod);
            return this;
        }

        public FilterBuilder subredditName(final String subredditName) {
            bundle.putString(FILTER_SUBREDDIT_NAME, subredditName);
            return this;
        }

        public FilterBuilder searchQuery(final String searchQuery) {
            bundle.putString(FILTER_SEARCH_QUERY, searchQuery);
            return this;
        }

        public FilterBuilder userName(final String userName) {
            bundle.putString(FILTER_USER_NAME, userName);
            return this;
        }

        public FilterBuilder userComments(final boolean userComments) {
            bundle.putBoolean(FILTER_USER_COMMENTS, userComments);
            return this;
        }

        public FilterBuilder userSubmitted(final boolean userSubmitted) {
            bundle.putBoolean(FILTER_USER_SUBMITTED, userSubmitted);
            return this;
        }

        public FilterBuilder userGilded(final boolean userGilded) {
            bundle.putBoolean(FILTER_USER_GILDED, userGilded);
            return this;
        }

        public Bundle build() {
            return bundle;
        }
    }
}
