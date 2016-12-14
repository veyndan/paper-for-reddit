package com.veyndan.paper.reddit.api.reddit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.veyndan.paper.reddit.api.reddit.model.Account2;
import com.veyndan.paper.reddit.api.reddit.model.CaptchaNew;
import com.veyndan.paper.reddit.api.reddit.model.Categories;
import com.veyndan.paper.reddit.api.reddit.model.Karma;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.MoreChildren;
import com.veyndan.paper.reddit.api.reddit.model.Prefs;
import com.veyndan.paper.reddit.api.reddit.model.RedditObject;
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

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Reddit {

    @NonNull private final RedditService redditService;

    public Reddit(@NonNull final Credentials credentials) {
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
                .create();

        final GsonConverterFactory jsonConverterFactory = GsonConverterFactory.create(gson);

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

    @NonNull
    private static HttpLoggingInterceptor loggingInterceptor() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        return loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    // ================================
    //             Account
    // ================================

    @NonNull
    public Single<Response<Account2>> me() {
        return redditService.me();
    }

    @NonNull
    public Single<Response<Thing<List<Karma>>>> myKarma() {
        return redditService.myKarma();
    }

    @NonNull
    public Single<Response<Prefs>> myPrefs() {
        return redditService.myPrefs();
    }

    @NonNull
    public Single<Response<Thing<Trophies>>> myTrophies() {
        return redditService.myTrophies();
    }

    // ================================
    //             Captcha
    // ================================

    @NonNull
    public Single<Response<CaptchaNew>> newCaptcha() {
        return redditService.newCaptcha("json");
    }

    @NonNull
    public Single<Response<Void>> idenCaptcha(@NonNull final String iden) {
        return redditService.idenCaptcha(iden);
    }

    // ================================
    //         Links & Comments
    // ================================

    @NonNull
    public Single<Response<Void>> hide(@NonNull final String... ids) {
        return hide(Arrays.asList(ids));
    }

    @NonNull
    public Single<Response<Void>> hide(@NonNull final List<String> ids) {
        return redditService.hide(TextUtils.join(",", ids));
    }

    @NonNull
    public Single<MoreChildren> moreChildren(@NonNull final List<String> children,
                                             @NonNull final String linkId) {
        return redditService.moreChildren("json", TextUtils.join(",", children), linkId);
    }

    @NonNull
    public Single<Response<Void>> save(@NonNull final String category, @NonNull final String id) {
        return redditService.save(category, id);
    }

    @NonNull
    public Single<Response<Categories>> savedCategories() {
        return redditService.savedCategories();
    }

    @NonNull
    public Single<Response<Void>> unsave(final String id) {
        return redditService.unsave(id);
    }

    @NonNull
    public Single<Response<Void>> vote(@NonNull final VoteDirection voteDirection,
                                       @NonNull final String id) {
        return redditService.vote(voteDirection, id);
    }

    // ================================
    //             Listings
    // ================================

    @NonNull
    private Single<Response<Thing<Listing>>> subredditComments(@NonNull final String subreddit,
                                                              @NonNull final String article) {
        return redditService.subredditComments(subreddit, article)
                .map(response -> {
                    final List<Thing<Listing>> things = response.body();
                    ((Submission) things.get(0).getData().getChildren().get(0)).getReplies().getData().getChildren().addAll(things.get(1).getData().getChildren());
                    return Response.success(things.get(0));
                });
    }

    @NonNull
    private Single<Response<Thing<Listing>>> subreddit(@NonNull final String subreddit,
                                                      @NonNull final Sort sort,
                                                      @NonNull final Maybe<TimePeriod> timePeriod) {
        final QueryBuilder query = new QueryBuilder();

        if (timePeriod.count().blockingGet() == 1L) {
            query.t(timePeriod.blockingGet());
        }

        final Single<Response<Thing<Listing>>> request = redditService.subreddit(subreddit, sort, query.build());
        return paginate(request, query);
    }

    // ================================
    //         Private Messages
    // ================================

    @NonNull
    public Single<Response<Thing<Listing>>> message(@NonNull final Message message) {
        return redditService.message(message);
    }

    // ================================
    //            Subreddits
    // ================================

    @NonNull
    public Single<Response<Thing<Listing>>> mySubreddits(@NonNull final MySubreddits mySubreddits) {
        return redditService.mySubreddits(mySubreddits);
    }

    @NonNull
    public Single<Response<Thing<Listing>>> subreddits(@NonNull final SubredditSort sort) {
        return redditService.subreddits(sort);
    }

    @NonNull
    public Single<Response<Thing<Subreddit>>> subredditAbout(@NonNull final String subreddit) {
        return redditService.subredditAbout(subreddit);
    }

    // ================================
    //              Users
    // ================================

    @NonNull
    public Single<Response<Thing<Listing>>> aboutSubreddit(
            @NonNull final String subreddit, @NonNull final AboutSubreddit where) {
        return redditService.aboutSubreddit(subreddit, where);
    }

    @NonNull
    public Single<Response<Thing<Trophies>>> userTrophies(@NonNull final String username) {
        return redditService.userTrophies(username);
    }

    @NonNull
    public Single<Response<Thing<Account2>>> userAbout(@NonNull final String username) {
        return redditService.userAbout(username);
    }

    @NonNull
    private Single<Response<Thing<Listing>>> user(@NonNull final String username,
                                                 @NonNull final User where,
                                                 @NonNull final Maybe<TimePeriod> timePeriod) {
        final QueryBuilder query = new QueryBuilder();

        if (timePeriod.count().blockingGet() == 1L) {
            query.t(timePeriod.blockingGet());
        }

        final Single<Response<Thing<Listing>>> request = redditService.user(username, where, query.build());
        return paginate(request, query);
    }

    public Single<Response<Thing<Listing>>> query(final Bundle params, final Sort sort) {
        final int nodeDepth = params.getInt(Filter.NODE_DEPTH, -1);

        final String commentsSubreddit = params.getString(Filter.COMMENTS_SUBREDDIT);
        final String commentsArticle = params.getString(Filter.COMMENTS_ARTICLE);

        final TimePeriod timePeriod = (TimePeriod) params.getSerializable(Filter.TIME_PERIOD);

        final String subredditName = params.getString(Filter.SUBREDDIT_NAME);

        final String userName = params.getString(Filter.USER_NAME);
        final boolean userComments = params.getBoolean(Filter.USER_COMMENTS, false);
        final boolean userSubmitted = params.getBoolean(Filter.USER_SUBMITTED, false);
        final boolean userGilded = params.getBoolean(Filter.USER_GILDED, false);
        final User userWhere = (User) params.getSerializable(Filter.USER_WHERE);

        if (nodeDepth == -1) {
            // TODO Get every post, along with its comments here i.e. the whole forest of Reddit
            throw new IllegalStateException();
        } else if (nodeDepth == 0) {
            if (commentsSubreddit == null && commentsArticle == null
                    && TextUtils.isEmpty(userName) && !userComments && !userSubmitted && !userGilded && userWhere == null) {
                return subreddit(MoreObjects.firstNonNull(subredditName, "all"), sort, Maybe.just(MoreObjects.firstNonNull(timePeriod, TimePeriod.ALL)));
            }

            if (commentsSubreddit == null && commentsArticle == null
                    && subredditName == null
                    && userName != null && userWhere != null) {
                return user(userName, userWhere, timePeriod == null ? Maybe.empty() : Maybe.just(timePeriod));
            }

            if (commentsSubreddit != null && commentsArticle != null
                    && timePeriod == null
                    && subredditName == null
                    && userName == null && !userComments && !userSubmitted && !userGilded && userWhere == null) {
                return subredditComments(commentsSubreddit, commentsArticle);
            }

            throw new IllegalStateException();
        } else {
            throw new IllegalStateException("Depths that aren't 0 or -1 aren't available to the Reddit API");
        }
    }

    @NonNull
    private static Single<Response<Thing<Listing>>> paginate(@NonNull final Single<Response<Thing<Listing>>> page,
                                                             @NonNull final QueryBuilder query) {
        return Single.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query1 -> !query1.build().containsKey("after") || query1.build().get("after") != null)
                .flatMapSingle(query1 -> page)
                .doOnSuccess(response -> query.after(response.body().getData().getAfter()));
    }

    public interface Filter {
        @NonNull String NODE_DEPTH = "node_depth";

        @NonNull String COMMENTS_SUBREDDIT = "comments_subreddit";
        @NonNull String COMMENTS_ARTICLE = "comments_article";

        @NonNull String TIME_PERIOD = "time_period";

        @NonNull String SUBREDDIT_NAME = "subreddit_name";

        @NonNull String USER_NAME = "user_name";
        @NonNull String USER_COMMENTS = "user_comments";
        @NonNull String USER_SUBMITTED = "user_submitted";
        @NonNull String USER_GILDED = "user_gilded";
        @NonNull String USER_WHERE = "user_where";
    }
}
