package com.veyndan.redditclient.api.reddit.network;

import com.veyndan.redditclient.api.reddit.model.Account2;
import com.veyndan.redditclient.api.reddit.model.CaptchaNew;
import com.veyndan.redditclient.api.reddit.model.Categories;
import com.veyndan.redditclient.api.reddit.model.Karma;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Prefs;
import com.veyndan.redditclient.api.reddit.model.Subreddit;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.model.Trophies;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface RedditService {

    // ================================
    //             Account
    // ================================

    @GET("api/v1/me")
    Observable<Response<Account2>> me();

    @GET("api/v1/me/karma")
    Observable<Response<Thing<List<Karma>>>> myKarma();

    @GET("api/v1/me/prefs")
    Observable<Response<Prefs>> myPrefs();

    @GET("api/v1/me/trophies")
    Observable<Response<Thing<Trophies>>> myTrophies();

    // ================================
    //             Captcha
    // ================================

    @FormUrlEncoded
    @POST("api/new_captcha")
    Observable<Response<CaptchaNew>> newCaptcha(@Field("api_type") String apiType);

    @GET("captcha/{iden}")
    Observable<Response<ResponseBody>> idenCaptcha(@Path("iden") String iden);

    // ================================
    //         Links & Comments
    // ================================

    @FormUrlEncoded
    @POST("api/hide")
    Observable<Response<ResponseBody>> hide(@Field("id") String ids);

    @FormUrlEncoded
    @POST("api/save")
    Observable<Response<ResponseBody>> save(@Field("category") String category, @Field("id") String id);

    @GET("api/saved_categories")
    Observable<Response<Categories>> savedCategories();

    @FormUrlEncoded
    @POST("api/unsave")
    Observable<Response<ResponseBody>> unsave(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/vote")
    Observable<Response<ResponseBody>> vote(
            @Field("dir") VoteDirection voteDirection, @Field("id") String id);

    // ================================
    //             Listings
    // ================================

    @GET("r/{subreddit}/comments/{article}")
    Observable<Response<List<Thing<Listing>>>> subredditComments(
            @Path("subreddit") String subreddit, @Path("article") String article);

    @GET("r/{subreddit}/{where}")
    Observable<Response<Thing<Listing>>> subreddit(
            @Path("subreddit") String subreddit, @Path("where") Sort sort,
            @QueryMap Map<String, String> queries);

    // ================================
    //         Private Messages
    // ================================

    @GET("message/{where}")
    Observable<Response<Thing<Listing>>> message(
            @Path("where") Message message);

    // ================================
    //            Subreddits
    // ================================

    @GET("r/{subreddit}/about")
    Observable<Response<Thing<Subreddit>>> subredditAbout(@Path("subreddit") String subreddit);

    @GET("subreddits/mine/{where}")
    Observable<Response<Thing<Listing>>> mySubreddits(@Path("where") MySubreddits mySubreddits);

    @GET("subreddits/{where}")
    Observable<Response<Thing<Listing>>> subreddits(@Path("where") SubredditSort sort);

    // ================================
    //              Users
    // ================================

    @GET("r/{subreddit}/about/{where}")
    Observable<Response<Thing<Listing>>> aboutSubreddit(
            @Path("subreddit") String subreddit, @Path("where") AboutSubreddit aboutSubreddit);

    @GET("api/v1/user/{username}/trophies")
    Observable<Response<Thing<Trophies>>> userTrophies(@Path("username") String username);

    @GET("user/{username}/about")
    Observable<Response<Thing<Account2>>> userAbout(@Path("username") String username);

    @GET("user/{username}/{where}")
    Observable<Response<Thing<Listing>>> user(
            @Path("username") String username, @Path("where") User where);
}
