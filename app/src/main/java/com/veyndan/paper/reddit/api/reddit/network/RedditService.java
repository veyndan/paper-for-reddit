package com.veyndan.paper.reddit.api.reddit.network;

import com.veyndan.paper.reddit.api.reddit.model.Account2;
import com.veyndan.paper.reddit.api.reddit.model.CaptchaNew;
import com.veyndan.paper.reddit.api.reddit.model.Categories;
import com.veyndan.paper.reddit.api.reddit.model.Karma;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.MoreChildren;
import com.veyndan.paper.reddit.api.reddit.model.Prefs;
import com.veyndan.paper.reddit.api.reddit.model.Subreddit;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.model.Trophies;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RedditService {

    // ================================
    //             Account
    // ================================

    @GET("api/v1/me")
    Single<Response<Account2>> me();

    @GET("api/v1/me/karma")
    Single<Response<Thing<List<Karma>>>> myKarma();

    @GET("api/v1/me/prefs")
    Single<Response<Prefs>> myPrefs();

    @GET("api/v1/me/trophies")
    Single<Response<Thing<Trophies>>> myTrophies();

    // ================================
    //             Captcha
    // ================================

    @FormUrlEncoded
    @POST("api/new_captcha")
    Single<Response<CaptchaNew>> newCaptcha(@Field("api_type") String apiType);

    @GET("captcha/{iden}")
    Single<Response<Void>> idenCaptcha(@Path("iden") String iden);

    // ================================
    //         Links & Comments
    // ================================

    @FormUrlEncoded
    @POST("api/hide")
    Single<Response<Void>> hide(@Field("id") String ids);

    @GET("api/morechildren")
    Single<MoreChildren> moreChildren(@Query("api_type") String apiType,
                                      @Query("children") String children,
                                      @Query("link_id") String linkId);

    @FormUrlEncoded
    @POST("api/save")
    Single<Response<Void>> save(@Field("category") String category, @Field("id") String id);

    @GET("api/saved_categories")
    Single<Response<Categories>> savedCategories();

    @FormUrlEncoded
    @POST("api/unsave")
    Single<Response<Void>> unsave(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/vote")
    Single<Response<Void>> vote(
            @Field("dir") VoteDirection voteDirection, @Field("id") String id);

    // ================================
    //             Listings
    // ================================

    @GET("r/{subreddit}/comments/{article}")
    Single<Response<List<Thing<Listing>>>> subredditComments(
            @Path("subreddit") String subreddit, @Path("article") String article);

    @GET("r/{subreddit}/{where}")
    Single<Response<Thing<Listing>>> subreddit(
            @Path("subreddit") String subreddit, @Path("where") Sort sort,
            @QueryMap Map<String, String> queries);

    // ================================
    //         Private Messages
    // ================================

    @GET("message/{where}")
    Single<Response<Thing<Listing>>> message(
            @Path("where") Message message);

    // ================================
    //              Search
    // ================================

    @GET("r/{subreddit}/search")
    Single<Response<Thing<Listing>>> search(@Path("subreddit") String subreddit,
                                            @QueryMap Map<String, String> queries);

    // ================================
    //            Subreddits
    // ================================

    @GET("r/{subreddit}/about")
    Single<Response<Thing<Subreddit>>> subredditAbout(@Path("subreddit") String subreddit);

    @GET("subreddits/mine/{where}")
    Single<Response<Thing<Listing>>> mySubreddits(@Path("where") MySubreddits mySubreddits);

    @GET("subreddits/{where}")
    Single<Response<Thing<Listing>>> subreddits(@Path("where") SubredditSort sort);

    // ================================
    //              Users
    // ================================

    @GET("r/{subreddit}/about/{where}")
    Single<Response<Thing<Listing>>> aboutSubreddit(
            @Path("subreddit") String subreddit, @Path("where") AboutSubreddit aboutSubreddit);

    @GET("api/v1/user/{username}/trophies")
    Single<Response<Thing<Trophies>>> userTrophies(@Path("username") String username);

    @GET("user/{username}/about")
    Single<Response<Thing<Account2>>> userAbout(@Path("username") String username);

    @GET("user/{username}/{where}")
    Single<Response<Thing<Listing>>> user(
            @Path("username") String username, @Path("where") User where,
            @QueryMap Map<String, String> queries);
}
