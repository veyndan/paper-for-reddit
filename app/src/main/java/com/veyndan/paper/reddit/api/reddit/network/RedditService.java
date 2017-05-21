package com.veyndan.paper.reddit.api.reddit.network;

import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RedditService {

    // ================================
    //         Links & Comments
    // ================================

    @FormUrlEncoded
    @POST("api/hide")
    Single<Response<Void>> hide(
            @Field("id") String ids);

    @FormUrlEncoded
    @POST("api/save")
    Single<Response<Void>> save(
            @Field("category") String category,
            @Field("id") String id);

    @FormUrlEncoded
    @POST("api/unsave")
    Single<Response<Void>> unsave(
            @Field("id") String id);

    @FormUrlEncoded
    @POST("api/vote")
    Single<Response<Void>> vote(
            @Field("dir") VoteDirection voteDirection,
            @Field("id") String id);

    // ================================
    //             Listings
    // ================================

    @GET("r/{subreddit}/comments/{article}")
    Single<Response<List<Thing<Listing>>>> subredditComments(
            @Path("subreddit") String subreddit,
            @Path("article") String article);

    @GET("r/{subreddit}/{where}")
    Single<Response<Thing<Listing>>> subreddit(
            @Path("subreddit") String subreddit,
            @Path("where") Sort sort,
            @QueryMap Map<String, String> queries);

    // ================================
    //              Search
    // ================================

    @GET("r/{subreddit}/search")
    Single<Response<Thing<Listing>>> search(
            @Path("subreddit") String subreddit,
            @QueryMap Map<String, String> queries);

    // ================================
    //              Users
    // ================================

    @GET("user/{username}/{where}")
    Single<Response<Thing<Listing>>> user(
            @Path("username") String username,
            @Path("where") User where,
            @QueryMap Map<String, String> queries);
}
