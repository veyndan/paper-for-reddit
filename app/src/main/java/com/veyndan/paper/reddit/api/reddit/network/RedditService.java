package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

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

    @NonNull
    @GET("api/v1/me")
    Single<Response<Account2>> me();

    @NonNull
    @GET("api/v1/me/karma")
    Single<Response<Thing<List<Karma>>>> myKarma();

    @NonNull
    @GET("api/v1/me/prefs")
    Single<Response<Prefs>> myPrefs();

    @NonNull
    @GET("api/v1/me/trophies")
    Single<Response<Thing<Trophies>>> myTrophies();

    // ================================
    //             Captcha
    // ================================

    @NonNull
    @FormUrlEncoded
    @POST("api/new_captcha")
    Single<Response<CaptchaNew>> newCaptcha(@NonNull @Field("api_type") String apiType);

    @NonNull
    @GET("captcha/{iden}")
    Single<Response<Void>> idenCaptcha(@NonNull @Path("iden") String iden);

    // ================================
    //         Links & Comments
    // ================================

    @NonNull
    @FormUrlEncoded
    @POST("api/hide")
    Single<Response<Void>> hide(@NonNull @Field("id") String ids);

    @NonNull
    @GET("api/morechildren")
    Single<MoreChildren> moreChildren(@NonNull @Query("api_type") String apiType,
                                      @NonNull @Query("children") String children,
                                      @NonNull @Query("link_id") String linkId);

    @NonNull
    @FormUrlEncoded
    @POST("api/save")
    Single<Response<Void>> save(@NonNull @Field("category") String category,
                                @NonNull @Field("id") String id);

    @NonNull
    @GET("api/saved_categories")
    Single<Response<Categories>> savedCategories();

    @NonNull
    @FormUrlEncoded
    @POST("api/unsave")
    Single<Response<Void>> unsave(@NonNull @Field("id") String id);

    @NonNull
    @FormUrlEncoded
    @POST("api/vote")
    Single<Response<Void>> vote(
            @NonNull @Field("dir") VoteDirection voteDirection, @NonNull @Field("id") String id);

    // ================================
    //             Listings
    // ================================

    @NonNull
    @GET("r/{subreddit}/comments/{article}")
    Single<Response<List<Thing<Listing>>>> subredditComments(
            @NonNull @Path("subreddit") String subreddit, @NonNull @Path("article") String article);

    @NonNull
    @GET("r/{subreddit}/{where}")
    Single<Response<Thing<Listing>>> subreddit(
            @NonNull @Path("subreddit") String subreddit, @NonNull @Path("where") Sort sort,
            @NonNull @QueryMap Map<String, String> queries);

    // ================================
    //         Private Messages
    // ================================

    @NonNull
    @GET("message/{where}")
    Single<Response<Thing<Listing>>> message(
            @NonNull @Path("where") Message message);

    // ================================
    //            Subreddits
    // ================================

    @NonNull
    @GET("r/{subreddit}/about")
    Single<Response<Thing<Subreddit>>> subredditAbout(@NonNull @Path("subreddit") String subreddit);

    @NonNull
    @GET("subreddits/mine/{where}")
    Single<Response<Thing<Listing>>> mySubreddits(@NonNull @Path("where") MySubreddits mySubreddits);

    @NonNull
    @GET("subreddits/{where}")
    Single<Response<Thing<Listing>>> subreddits(@NonNull @Path("where") SubredditSort sort);

    // ================================
    //              Users
    // ================================

    @NonNull
    @GET("r/{subreddit}/about/{where}")
    Single<Response<Thing<Listing>>> aboutSubreddit(
            @NonNull @Path("subreddit") String subreddit,
            @NonNull @Path("where") AboutSubreddit aboutSubreddit);

    @NonNull
    @GET("api/v1/user/{username}/trophies")
    Single<Response<Thing<Trophies>>> userTrophies(@NonNull @Path("username") String username);

    @NonNull
    @GET("user/{username}/about")
    Single<Response<Thing<Account2>>> userAbout(@NonNull @Path("username") String username);

    @NonNull
    @GET("user/{username}/{where}")
    Single<Response<Thing<Listing>>> user(
            @NonNull @Path("username") String username, @NonNull @Path("where") User where,
            @NonNull @QueryMap Map<String, String> queries);
}
