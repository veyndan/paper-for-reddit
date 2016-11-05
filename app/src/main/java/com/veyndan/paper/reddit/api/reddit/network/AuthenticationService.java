package com.veyndan.paper.reddit.api.reddit.network;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationService {
    @FormUrlEncoded
    @POST("api/v1/access_token")
    Single<AccessToken> getAccessToken(
            @Field("grant_type") String grantType, @Field("username") String username,
            @Field("password") String password);
}
