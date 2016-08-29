package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.api.imgur.network.ImgurService;
import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.Source;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.post.model.media.Image;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

final class ImgurMutatorFactory implements MutatorFactory {

    private final Pattern pattern = Pattern.compile("^https?://(?:www\\.)?(i\\.)?imgur\\.com/.*$");
    private final Pattern albumPattern = Pattern.compile("^https?://(?:www\\.)?imgur\\.com/(?:a|gallery)/(.*)$");

    static ImgurMutatorFactory create() {
        return new ImgurMutatorFactory();
    }

    private ImgurMutatorFactory() {
    }

    @Override
    public boolean applicable(final Post post) {
        return post.submission instanceof Link && pattern.matcher(post.submission.linkUrl).matches();
    }

    @Override
    public void mutate(final Post post) {
        final Matcher matcher = pattern.matcher(post.submission.linkUrl);
        if (!matcher.matches()) {
            throw new IllegalStateException("Should match as matches in applicable()");
        }

        final Matcher albumMatcher = albumPattern.matcher(post.submission.linkUrl);
        final boolean isAlbum = albumMatcher.matches();
        final boolean isDirectImage = matcher.group(1) != null;

        if (!isAlbum && !isDirectImage) {
            // TODO .gifv links are HTML 5 videos so the PostHint should be set accordingly.
            if (!post.submission.linkUrl.endsWith(".gifv")) {
                post.submission.linkUrl = singleImageUrlToDirectImageUrl(post.submission.linkUrl);

                ((Link) post.submission).setPostHint(PostHint.IMAGE);
            }
        }

        if (isAlbum) {
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

            final String id = albumMatcher.group(1);

            post.setMediaObservable(
                    imgurService.album(id)
                            .flatMap(basic -> Observable.from(basic.data.images))
                            .map(image -> new Image(image.link, image.width, image.height))
            );
        } else {
            final boolean imageDimensAvailable = !((Link) post.submission).preview.images.isEmpty();

            int width = 0;
            int height = 0;
            if (imageDimensAvailable) {
                final Source source = ((Link) post.submission).preview.images.get(0).source;
                width = source.width;
                height = source.height;
            }

            final Image image = new Image(post.submission.linkUrl, width, height);
            post.setMediaObservable(Observable.just(image));
        }
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
