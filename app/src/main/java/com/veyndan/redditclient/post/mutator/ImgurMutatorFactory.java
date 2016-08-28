package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.api.imgur.network.ImgurService;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

final class ImgurMutatorFactory implements MutatorFactory {

    static ImgurMutatorFactory create() {
        return new ImgurMutatorFactory();
    }

    private ImgurMutatorFactory() {
    }

    @Override
    public boolean applicable(final Post post) {
        final String urlHost = HttpUrl.parse(post.submission.linkUrl).host();
        return post.submission instanceof Link &&
                urlHost.equals("imgur.com") || urlHost.equals("i.imgur.com");
    }

    @Override
    public void mutate(final Post post) {
        if (!isAlbum(post.submission.linkUrl) && !isDirectImage(post.submission.linkUrl)) {
            // TODO .gifv links are HTML 5 videos so the PostHint should be set accordingly.
            if (!post.submission.linkUrl.endsWith(".gifv")) {
                post.submission.linkUrl = singleImageUrlToDirectImageUrl(post.submission.linkUrl);
                post.setImageObservable(Observable.just(new Image(post.submission.linkUrl)));
                ((Link) post.submission).setPostHint(PostHint.IMAGE);
            }
        }

        if (isAlbum(post.submission.linkUrl)) {
            ((Link) post.submission).setPostHint(PostHint.IMAGE);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Client-ID " + Config.IMGUR_CLIENT_ID)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.imgur.com/3/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            final ImgurService imgurService = retrofit.create(ImgurService.class);

            final String id;
            if (post.submission.linkUrl.contains("/a/")) {
                id = post.submission.linkUrl.split("/a/")[1];
            } else if (post.submission.linkUrl.contains("/gallery/")) {
                id = post.submission.linkUrl.split("/gallery/")[1];
            } else {
                throw new IllegalStateException();
            }

            post.setImageObservable(
                    imgurService.album(id)
                            .flatMap(basic -> Observable.from(basic.data.images))
                            .map(image -> new Image(image.link, image.width, image.height))
            );
        }
    }

    private boolean isDirectImage(final String url) {
        final String urlHost = HttpUrl.parse(url).host();
        return urlHost.equals("i.imgur.com");
    }

    private boolean isAlbum(final String url) {
        return url.contains("/a/") || url.contains("/gallery/");
    }

    /**
     * Returns a direct image url
     * (e.g. <a href="http://i.imgur.com/1AGVxLl.png">http://i.imgur.com/1AGVxLl.png</a>) from a
     * single image url (e.g. <a href="http://imgur.com/1AGVxLl">http://imgur.com/1AGVxLl</a>)
     *
     * @param url The single image url.
     * @return The direct image url.
     */
    private String singleImageUrlToDirectImageUrl(final String url) {
        return HttpUrl.parse(url).newBuilder().host("i.imgur.com").build().toString() + ".png";
    }
}
