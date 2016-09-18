package com.veyndan.redditclient.api.reddit;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.veyndan.redditclient.api.reddit.model.Account2;
import com.veyndan.redditclient.api.reddit.model.CaptchaNew;
import com.veyndan.redditclient.api.reddit.model.Categories;
import com.veyndan.redditclient.api.reddit.model.Karma;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.MoreChildren;
import com.veyndan.redditclient.api.reddit.model.Prefs;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Subreddit;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.model.Trophies;
import com.veyndan.redditclient.api.reddit.network.AboutSubreddit;
import com.veyndan.redditclient.api.reddit.network.AuthenticationService;
import com.veyndan.redditclient.api.reddit.network.Credentials;
import com.veyndan.redditclient.api.reddit.network.Message;
import com.veyndan.redditclient.api.reddit.network.MySubreddits;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.RedditService;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.api.reddit.network.SubredditSort;
import com.veyndan.redditclient.api.reddit.network.User;
import com.veyndan.redditclient.api.reddit.network.VoteDirection;
import com.veyndan.redditclient.api.reddit.network.interceptor.AccessTokenInterceptor;
import com.veyndan.redditclient.api.reddit.network.interceptor.AuthorizationInterceptor;
import com.veyndan.redditclient.api.reddit.network.interceptor.RawJsonInterceptor;
import com.veyndan.redditclient.api.reddit.network.interceptor.UserAgentInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public final class Reddit {

    private final RedditService redditService;

    private Reddit(final Builder builder) {
        final Credentials credentials = builder.credentials;

        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
                .create();

        final OkHttpClient.Builder authenticationClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AuthorizationInterceptor(credentials));

        for (final Interceptor interceptor : builder.networkInterceptors) {
            authenticationClientBuilder.addNetworkInterceptor(interceptor);
        }

        final Retrofit authenticatorRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(authenticationClientBuilder.build())
                .build();

        final AuthenticationService authenticationService = authenticatorRetrofit.create(AuthenticationService.class);

        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AccessTokenInterceptor(authenticationService, credentials))
                .addInterceptor(new RawJsonInterceptor());

        if (Config.DEBUG) clientBuilder.addInterceptor(loggingInterceptor());

        for (final Interceptor interceptor : builder.networkInterceptors) {
            clientBuilder.addNetworkInterceptor(interceptor);
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://oauth.reddit.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
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

    public Observable<Response<Account2>> me() {
        return redditService.me();
    }

    public Observable<Response<Thing<List<Karma>>>> myKarma() {
        return redditService.myKarma();
    }

    public Observable<Response<Prefs>> myPrefs() {
        return redditService.myPrefs();
    }

    public Observable<Response<Thing<Trophies>>> myTrophies() {
        return redditService.myTrophies();
    }

    // ================================
    //             Captcha
    // ================================

    public Observable<Response<CaptchaNew>> newCaptcha() {
        return redditService.newCaptcha("json");
    }

    public Observable<Response<ResponseBody>> idenCaptcha(final String iden) {
        return redditService.idenCaptcha(iden);
    }

    // ================================
    //         Links & Comments
    // ================================

    public Observable<Response<ResponseBody>> hide(final String... ids) {
        return hide(Arrays.asList(ids));
    }

    public Observable<Response<ResponseBody>> hide(final List<String> ids) {
        return redditService.hide(TextUtils.join(",", ids));
    }

    public Observable<MoreChildren> moreChildren(final List<String> children, final String linkId) {
        return redditService.moreChildren("json", TextUtils.join(",", children), linkId);
    }

    public Observable<Response<ResponseBody>> save(final String category, final String id) {
        return redditService.save(category, id);
    }

    public Observable<Response<Categories>> savedCategories() {
        return redditService.savedCategories();
    }

    public Observable<Response<ResponseBody>> unsave(final String id) {
        return redditService.unsave(id);
    }

    public Observable<Response<ResponseBody>> vote(final VoteDirection voteDirection, final String id) {
        return redditService.vote(voteDirection, id);
    }

    // ================================
    //             Listings
    // ================================

    public Observable<Response<List<Thing<Listing>>>> subredditComments(final String subreddit, final String article) {
        return redditService.subredditComments(subreddit, article);
    }

    public Observable<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort) {
        return subreddit(subreddit, sort, new QueryBuilder());
    }

    public Observable<Response<Thing<Listing>>> subreddit(final String subreddit, final Sort sort,
                                                          final QueryBuilder queryBuilder) {
        return redditService.subreddit(subreddit, sort, queryBuilder.build());
    }

    // ================================
    //         Private Messages
    // ================================

    public Observable<Response<Thing<Listing>>> message(final Message message) {
        return redditService.message(message);
    }

    // ================================
    //            Subreddits
    // ================================

    public Observable<Response<Thing<Listing>>> mySubreddits(final MySubreddits mySubreddits) {
        return redditService.mySubreddits(mySubreddits);
    }

    public Observable<Response<Thing<Listing>>> subreddits(final SubredditSort sort) {
        return redditService.subreddits(sort);
    }

    public Observable<Response<Thing<Subreddit>>> subredditAbout(final String subreddit) {
        return redditService.subredditAbout(subreddit);
    }

    // ================================
    //              Users
    // ================================

    public Observable<Response<Thing<Listing>>> aboutSubreddit(
            final String subreddit, final AboutSubreddit where) {
        return redditService.aboutSubreddit(subreddit, where);
    }

    public Observable<Response<Thing<Trophies>>> userTrophies(final String username) {
        return redditService.userTrophies(username);
    }

    public Observable<Response<Thing<Account2>>> userAbout(final String username) {
        return redditService.userAbout(username);
    }

    public Observable<Response<Thing<Listing>>> user(final String username, final User where) {
        return user(username, where, new QueryBuilder());
    }

    public Observable<Response<Thing<Listing>>> user(final String username, final User where,
                                                     final QueryBuilder queryBuilder) {
        return redditService.user(username, where, queryBuilder.build());
    }

    public static class Builder {

        private final Credentials credentials;
        private final List<Interceptor> networkInterceptors = new ArrayList<>();

        public Builder(final Credentials credentials) {
            this.credentials = credentials;
        }

        public Builder addNetworkInterceptor(final Interceptor interceptor) {
            networkInterceptors.add(interceptor);
            return this;
        }

        public Reddit build() {
            return new Reddit(this);
        }
    }
}
