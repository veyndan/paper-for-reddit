package com.veyndan.redditclient.post;

import com.veyndan.redditclient.MvpView;
import com.veyndan.redditclient.post.model.Post;

import java.util.List;

import rx.Observable;

public interface PostMvpView extends MvpView {

    void showPosts(List<Post> posts);

    Observable<Boolean> getNextPageTrigger();
}
