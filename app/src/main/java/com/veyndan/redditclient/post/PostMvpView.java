package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.api.reddit.model.RedditObject;

import java.util.List;

import rx.Observable;

public interface PostMvpView extends MvpView {

    void showPosts(List<RedditObject> posts);

    Observable<Boolean> getNextPageTrigger();
}
