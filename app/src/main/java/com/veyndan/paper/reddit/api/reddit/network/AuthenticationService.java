package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationService {

    @NonNull
    @FormUrlEncoded
    @POST("api/v1/access_token")
    Single<AccessToken> getAccessToken(
            @NonNull @Field("grant_type") String grantType,
            @NonNull @Field("username") String username,
            @NonNull @Field("password") String password);
}
