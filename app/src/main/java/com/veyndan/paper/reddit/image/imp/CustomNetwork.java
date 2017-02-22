package com.veyndan.paper.reddit.image.imp;

import com.veyndan.paper.reddit.image.Network;

import java.io.InputStream;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class CustomNetwork implements Network {

    @Override
    public Single<InputStream> getImageAsInputStream(final String url) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://example.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        final ImageService imageService = retrofit.create(ImageService.class);

        return imageService.getImage(url)
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                .map(ResponseBody::byteStream);
    }

    private interface ImageService {

        @GET
        Single<Response<ResponseBody>> getImage(@Url String url);
    }
}
