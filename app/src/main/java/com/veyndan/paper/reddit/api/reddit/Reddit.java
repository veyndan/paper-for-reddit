package com.veyndan.paper.reddit.api.reddit;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.MoreObjects;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import com.veyndan.paper.reddit.api.reddit.network.AccessToken;
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
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // TODO Move or remove this
    private static class ErrorMessageBuilder {

        private String cause = "";
        private String resolution = "";

        private ErrorMessageBuilder cause(final String cause) {
            this.cause = "Cause: \"" + cause + '"';
            return this;
        }

        private ErrorMessageBuilder resolution(final String resolution) {
            this.resolution = "Resolution: \"" + resolution + '"';
            return this;
        }

        private String build() {
            return cause + ' ' + resolution;
        }
    }

    // TODO Move code somewhere else? In credentials maybe, but not really a credential.
    public Reddit(final Credentials credentials, final String code) {
        // TODO Make OAuth2 flow more sequential making it more understandable.

        // TODO Are too many unrelated adapters here?
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
                // TODO Make class for adapter
                .registerTypeAdapter(AccessToken.class, (JsonDeserializer<AccessToken>) (json, typeOfT, context) -> {
                    final JsonObject jsonObject = json.getAsJsonObject();
                    final JsonElement errorJsonElement = jsonObject.get("error");
                    if (errorJsonElement != null) {
                        final String error = errorJsonElement.getAsString();
                        // TODO Define this in enum, can move ErrorMessageBuilder there
                        switch (error) {
                            case "unsupported_grant_type":
                                throw new IllegalStateException(new ErrorMessageBuilder()
                                        .cause("`grant_type` parameter was invalid or Http Content type was not set correctly")
                                        .resolution("Verify that the `grant_type` sent is supported and make sure the content type of the http message is set to `application/x-www-form-urlencoded`")
                                        .build());
                            case "invalid_request":
                                throw new IllegalStateException(new ErrorMessageBuilder()
                                        .cause("You didn't include the `code` parameter")
                                        .resolution("Include the `code` parameter in the POST data")
                                        .build());
                            case "invalid_grant":
                                throw new IllegalStateException(new ErrorMessageBuilder()
                                        .cause("The `code` has expired or already been used")
                                        .resolution("Ensure that you are not attempting to re-use old `code`s - they are one time use.")
                                        .build());
                            default:
                                throw new IllegalStateException("Unknown error type: " + error);
                        }
                    }

                    // TODO Large memory footprint. context.deserialize() causes infinite loop.
                    final Gson gsonInner = new GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create();

                    return gsonInner.fromJson(json, typeOfT);
                })
                .create();

        final GsonConverterFactory jsonConverterFactory = GsonConverterFactory.create(gson);

        final RxJava2CallAdapterFactory rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();

        final OkHttpClient client = new OkHttpClient();

        final OkHttpClient.Builder authenticationClientBuilder = client.newBuilder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AuthorizationInterceptor(credentials))
                // TODO Move interceptor elsewhere
                .addInterceptor(chain -> {
                    final Request request = chain.request();
                    final okhttp3.Response response = chain.proceed(request);

                    if (response.code() == 401) {
                        throw new IllegalStateException(new ErrorMessageBuilder()
                                .cause("Client credentials sent as HTTP Basic Authorization were invalid")
                                .resolution("Verify that you are properly sending HTTP Basic Authorization headers and that your credentials are correct")
                                .build());
                    }

                    return response;
                });

        final Retrofit authenticatorRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/api/v1/")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(authenticationClientBuilder.build())
                .build();

        final AuthenticationService authenticationService = authenticatorRetrofit.create(AuthenticationService.class);

        final OkHttpClient.Builder clientBuilder = client.newBuilder()
                .addInterceptor(new UserAgentInterceptor(credentials.getUserAgent()))
                .addInterceptor(new AccessTokenInterceptor(authenticationService, credentials, code))
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
