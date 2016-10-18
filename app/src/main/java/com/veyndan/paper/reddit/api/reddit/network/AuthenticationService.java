package com.veyndan.paper.reddit.api.reddit.network;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface AuthenticationService {
    @FormUrlEncoded
    @POST("api/v1/access_token")
    Observable<AccessToken> getAccessToken(
            @Field("grant_type") String grantType, @Field("username") String username,
            @Field("password") String password);
}
