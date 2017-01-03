package com.veyndan.paper.reddit.api.reddit.network;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenticationService {

    @FormUrlEncoded
    @POST("access_token")
    Single<AccessToken> getAccessToken(@Header("Authorization") String authorization,
                                       @Field("grant_type") String grantType,
                                       @Field("code") String code,
                                       @Field("redirect_uri") String redirectUri);
}
