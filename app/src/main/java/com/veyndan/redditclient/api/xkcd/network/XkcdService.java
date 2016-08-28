package com.veyndan.redditclient.api.xkcd.network;

import com.veyndan.redditclient.api.xkcd.model.XkcdComic;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface XkcdService {

    @GET("{num}/info.0.json")
    Observable<XkcdComic> num(@Path("num") int num);
}
