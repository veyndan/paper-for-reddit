package com.veyndan.paper.reddit.api.imgur.network;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.imgur.model.Album;
import com.veyndan.paper.reddit.api.imgur.model.Basic;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImgurService {

    @NonNull
    @GET("album/{id}")
    Single<Basic<Album>> album(@NonNull @Path("id") String id);
}
