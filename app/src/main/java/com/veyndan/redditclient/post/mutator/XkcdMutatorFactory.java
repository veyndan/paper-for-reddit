package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.xkcd.network.XkcdService;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

final class XkcdMutatorFactory implements MutatorFactory {

    private final Pattern pattern = Pattern.compile("^https?://(www\\.)?xkcd\\.com/(\\d+)/?$");

    static XkcdMutatorFactory create() {
        return new XkcdMutatorFactory();
    }

    private XkcdMutatorFactory() {
    }

    @Override
    public boolean applicable(final Post post) {
        final Matcher matcher = pattern.matcher(post.submission.linkUrl);
        return post.submission instanceof Link && matcher.matches();
    }

    @Override
    public void mutate(final Post post) {
        final Matcher matcher = pattern.matcher(post.submission.linkUrl);
        if (matcher.matches()) {
            final int comicNum = Integer.parseInt(matcher.group(2));

            final Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://xkcd.com")
                    .build();

            final XkcdService xkcdService = retrofit.create(XkcdService.class);

            final Observable<Image> imageObservable = xkcdService.num(comicNum)
                    .map(comic -> new Image(comic.getImg()));

            ((Link) post.submission).setPostHint(PostHint.IMAGE);

            post.setImageObservable(imageObservable);
        }
    }
}
