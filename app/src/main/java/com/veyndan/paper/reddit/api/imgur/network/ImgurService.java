package com.veyndan.paper.reddit.api.imgur.network;

import com.veyndan.paper.reddit.api.imgur.model.Album;
import com.veyndan.paper.reddit.api.imgur.model.Basic;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ImgurService {

    @GET("album/{id}")
    Observable<Basic<Album>> album(@Path("id") String id);
}
