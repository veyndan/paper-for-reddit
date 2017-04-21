package com.veyndan.paper.reddit.api.xkcd.network;

import android.support.annotation.NonNull;

import com.veyndan.paper.reddit.api.xkcd.model.XkcdComic;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface XkcdService {

    @NonNull
    @GET("{num}/info.0.json")
    Single<Response<XkcdComic>> num(
            @Path("num") int num);
}
