package com.veyndan.paper.reddit.api.reddit;

import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.auto.value.AutoValue;
import com.google.common.base.MoreObjects;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.veyndan.paper.reddit.BuildConfig;
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

    public static final String FILTER = "filter";

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
                .addInterceptor(new UserAgentInterceptor(credentials.userAgent()))
                .addInterceptor(new AuthorizationInterceptor(credentials));

        final Retrofit authenticatorRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(authenticationClientBuilder.build())
                .build();

        final AuthenticationService authenticationService = authenticatorRetrofit.create(AuthenticationService.class);

        final OkHttpClient.Builder clientBuilder = client.newBuilder()
                .addInterceptor(new UserAgentInterceptor(credentials.userAgent()))
                .addInterceptor(new AccessTokenInterceptor(authenticationService, credentials))
                .addInterceptor(new RawJsonInterceptor());

        if (BuildConfig.DEBUG) {
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

    public Single<Response<MoreChildren>> moreChildren(final List<String> children, final String linkId) {
        return redditService.moreChildren(TextUtils.join(",", children), linkId);
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

    @AutoValue
    public abstract static class Filter implements Parcelable {

        @IntRange(from = -1)
        public abstract int nodeDepth();

        public abstract String commentsSubreddit();

        public abstract String commentsArticle();

        @Nullable
        public abstract TimePeriod timePeriod();

        public abstract String subredditName();

        public abstract String searchQuery();

        public abstract String userName();

        public abstract boolean userComments();

        public abstract boolean userSubmitted();

        public abstract boolean userGilded();

        public static Builder builder() {
            return new AutoValue_Reddit_Filter.Builder()
                    .nodeDepth(-1)
                    .commentsSubreddit("")
                    .commentsArticle("")
                    .subredditName("")
                    .searchQuery("")
                    .userName("")
                    .userComments(false)
                    .userSubmitted(false)
                    .userGilded(false);
        }

        @AutoValue.Builder
        public abstract static class Builder {

            public abstract Builder nodeDepth(@IntRange(from = -1) final int nodeDepth);

            public abstract Builder commentsSubreddit(final String commentsSubreddit);

            public abstract Builder commentsArticle(final String commentsArticle);

            public abstract Builder timePeriod(@Nullable final TimePeriod timePeriod);

            public abstract Builder subredditName(final String subredditName);

            public abstract Builder searchQuery(final String searchQuery);

            public abstract Builder userName(final String userName);

            public abstract Builder userComments(final boolean userComments);

            public abstract Builder userSubmitted(final boolean userSubmitted);

            public abstract Builder userGilded(final boolean userGilded);

            public abstract Filter build();
        }
    }

    public Single<Response<Thing<Listing>>> query(final Filter filter, final Sort sort) {
        if (filter.nodeDepth() == -1) {
            // TODO Get every post, along with its comments here i.e. the whole forest of Reddit
            throw new IllegalStateException();
        } else if (filter.nodeDepth() == 0) {
            if (filter.commentsSubreddit().isEmpty() && filter.commentsArticle().isEmpty()
                    && filter.searchQuery().isEmpty()
                    && filter.userName().isEmpty() && !filter.userComments() && !filter.userSubmitted() && !filter.userGilded()) {
                return subreddit(MoreObjects.firstNonNull(filter.subredditName(), "all"), sort, MoreObjects.firstNonNull(filter.timePeriod(), TimePeriod.ALL));
            }

            if (filter.commentsSubreddit().isEmpty() && filter.commentsArticle().isEmpty()
                    && filter.subredditName().isEmpty()
                    && filter.searchQuery().isEmpty()
                    && filter.userName().length() > 1) {
                final User userWhere = toUser(filter.userComments(), filter.userSubmitted(), filter.userGilded());
                return user(filter.userName(), userWhere, filter.timePeriod());
            }

            if (filter.commentsSubreddit().isEmpty() && filter.commentsArticle().isEmpty()
                    && filter.timePeriod() == null
                    && filter.subredditName().length() > 0
                    && filter.searchQuery().length() > 0
                    && filter.userName().isEmpty() && !filter.userComments() && !filter.userSubmitted() && !filter.userGilded()) {
                return search(filter.subredditName(), filter.searchQuery());
            }

            if (filter.commentsSubreddit().length() > 1 && filter.commentsArticle().length() > 1
                    && filter.timePeriod() == null
                    && filter.subredditName().isEmpty()
                    && filter.searchQuery().isEmpty()
                    && filter.userName().isEmpty() && !filter.userComments() && !filter.userSubmitted() && !filter.userGilded()) {
                return subredditComments(filter.commentsSubreddit(), filter.commentsArticle());
            }

            throw new IllegalStateException();
        } else {
            throw new IllegalStateException("Depths that aren't 0 or -1 aren't available to the Reddit API");
        }
    }

    private static User toUser(final boolean comments, final boolean submitted, final boolean gilded) {
        if (comments == submitted && gilded) {
            return User.GILDED;
        } else if (comments != submitted && gilded) {
            throw new UnsupportedOperationException("User state unsure");
        } else if (comments && submitted) {
            return User.OVERVIEW;
        } else if (comments) {
            return User.COMMENTS;
        } else if (submitted) {
            return User.SUBMITTED;
        } else {
            return User.OVERVIEW;
        }
    }

    private static Single<Response<Thing<Listing>>> paginate(final Single<Response<Thing<Listing>>> page, final QueryBuilder query) {
        return Single.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query1 -> !query1.build().containsKey("after") || query1.build().get("after") != null)
                .flatMapSingle(query1 -> page)
                .doOnSuccess(response -> query.after(response.body().data.after));
    }
}
