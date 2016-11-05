package com.veyndan.paper.reddit.api.xkcd.network;

import com.veyndan.paper.reddit.api.xkcd.model.XkcdComic;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface XkcdService {

    @GET("{num}/info.0.json")
    Single<XkcdComic> num(@Path("num") int num);
}
