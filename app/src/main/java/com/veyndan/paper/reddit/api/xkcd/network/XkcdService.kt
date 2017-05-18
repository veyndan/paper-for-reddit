package com.veyndan.paper.reddit.api.xkcd.network

import com.veyndan.paper.reddit.api.xkcd.model.XkcdComic
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdService {

    @GET("{num}/info.0.json")
    fun num(@Path("num") num: Int): Single<Response<XkcdComic>>
}
