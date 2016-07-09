package com.veyndan.redditclient;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ImgurService {

    @GET("album/{id}")
    Observable<Basic<Album>> album(@Path("id") String id);
}
