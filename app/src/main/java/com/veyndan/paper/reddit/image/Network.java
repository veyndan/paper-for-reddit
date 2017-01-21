package com.veyndan.paper.reddit.image;

import java.io.InputStream;

import io.reactivex.Single;

public interface Network {

    Single<InputStream> getImageAsInputStream(String url);
}
