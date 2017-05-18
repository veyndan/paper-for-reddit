package com.veyndan.paper.reddit.api.reddit.network

import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface RedditService {

    // ================================
    //         Links & Comments
    // ================================

    @FormUrlEncoded
    @POST("api/hide")
    fun hide(
            @Field("id") ids: String): Single<Response<Void>>

    @FormUrlEncoded
    @POST("api/save")
    fun save(
            @Field("category") category: String,
            @Field("id") id: String): Single<Response<Void>>

    @FormUrlEncoded
    @POST("api/unsave")
    fun unsave(
            @Field("id") id: String): Single<Response<Void>>

    @FormUrlEncoded
    @POST("api/vote")
    fun vote(
            @Field("dir") voteDirection: VoteDirection,
            @Field("id") id: String): Single<Response<Void>>

    // ================================
    //             Listings
    // ================================

    @GET("r/{subreddit}/comments/{article}")
    fun subredditComments(
            @Path("subreddit") subreddit: String,
            @Path("article") article: String): Single<Response<List<Thing<Listing>>>>

    @GET("r/{subreddit}/{where}")
    fun subreddit(
            @Path("subreddit") subreddit: String,
            @Path("where") sort: Sort,
            @QueryMap queries: Map<String, String>): Single<Response<Thing<Listing>>>

    // ================================
    //              Search
    // ================================

    @GET("r/{subreddit}/search")
    fun search(
            @Path("subreddit") subreddit: String,
            @QueryMap queries: Map<String, String>): Single<Response<Thing<Listing>>>

    // ================================
    //              Users
    // ================================

    @GET("user/{username}/{where}")
    fun user(
            @Path("username") username: String,
            @Path("where") where: User,
            @QueryMap queries: Map<String, String>): Single<Response<Thing<Listing>>>
}
