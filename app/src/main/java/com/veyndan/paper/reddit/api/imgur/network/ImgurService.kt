package com.veyndan.paper.reddit.api.imgur.network

import com.veyndan.paper.reddit.api.imgur.model.Album
import com.veyndan.paper.reddit.api.imgur.model.Basic
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ImgurService {

    @GET("album/{id}")
    fun album(@Path("id") id: String): Single<Response<Basic<Album>>>
}
