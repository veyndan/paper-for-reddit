package com.veyndan.paper.reddit.api.reddit.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthenticationService {

    @FormUrlEncoded
    @POST("api/v1/access_token")
    fun getAccessToken(
            @Field("grant_type") grantType: String,
            @Field("username") username: String,
            @Field("password") password: String): Single<Response<AccessToken>>
}
