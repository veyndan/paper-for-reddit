package com.veyndan.redditclient.post;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;

import retrofit2.Response;
import rx.Observable;

public interface PostsFilter {

    Observable<Response<Thing<Listing>>> getRequestObservable(Reddit reddit);

    void setAfter(String after);
}
