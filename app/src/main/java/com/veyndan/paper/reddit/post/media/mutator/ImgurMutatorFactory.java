package com.veyndan.paper.reddit.post.media.mutator;

import android.support.annotation.StringRes;
import android.util.Size;

import com.veyndan.paper.reddit.Config;
import com.veyndan.paper.reddit.api.imgur.network.ImgurService;
import com.veyndan.paper.reddit.api.reddit.model.PostHint;
import com.veyndan.paper.reddit.api.reddit.model.Source;
import com.veyndan.paper.reddit.post.media.model.Image;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

final class ImgurMutatorFactory implements MutatorFactory {

    private static final Pattern PATTERN = Pattern.compile("^https?://(?:m\\.|www\\.)?(i\\.)?imgur\\.com/(a/|gallery/)?(.*)$");

    static ImgurMutatorFactory create() {
        return new ImgurMutatorFactory();
    }

    private ImgurMutatorFactory() {
    }

    @Override
    public Maybe<Post> mutate(final Post post) {
        final Matcher matcher = PATTERN.matcher(post.getLinkUrl());

        return Single.just(post)
                .filter(post1 -> matcher.matches())
                .flatMap(post1 -> {
                    final boolean isAlbum = matcher.group(2) != null;
                    final boolean isDirectImage = matcher.group(1) != null;

                    if (!isAlbum && !isDirectImage) {
                        // TODO .gifv links are HTML 5 videos so the PostHint should be set accordingly.
                        if (!post1.getLinkUrl().endsWith(".gifv")) {
                            post1.setLinkUrl(singleImageUrlToDirectImageUrl(post1.getLinkUrl()));

                            post.setPostHint(PostHint.IMAGE);
                        }
                    }

                    if (isAlbum) {
                        post.setPostHint(PostHint.IMAGE);

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
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(MoshiConverterFactory.create())
                                .client(client)
                                .build();

                        final ImgurService imgurService = retrofit.create(ImgurService.class);

                        final String id = matcher.group(3);

                        return imgurService.album(id)
                                .flatMapObservable(basic -> Observable.fromIterable(basic.getData().images()))
                                .map(image -> Image.create(image.getLink(), new Size(image.getWidth(), image.getHeight())))
                                .toList()
                                .toMaybe();
                    } else {
                        final boolean imageDimensAvailable = !post.getPreview().images.isEmpty();

                        final String url = post.getLinkUrl().endsWith(".gifv") && imageDimensAvailable
                                ? post.getPreview().images.get(0).source.url
                                : post.getLinkUrl();

                        final Size size;
                        if (imageDimensAvailable) {
                            final Source source = post.getPreview().images.get(0).source;
                            size = new Size(source.width, source.height);
                        } else {
                            size = new Size(0, 0);
                        }

                        @StringRes final int type = post.getLinkUrl().endsWith(".gif") || post.getLinkUrl().endsWith(".gifv")
                                ? Image.IMAGE_TYPE_GIF
                                : Image.IMAGE_TYPE_STANDARD;

                        return Maybe.just(Collections.singletonList(Image.create(url, size, type)));
                    }
                }, (post1, images) -> {
                    post1.getMedias().addAll(images);
                    return post1;
                });
    }

    /**
     * Returns a direct image url
     * (e.g. <a href="http://i.imgur.com/1AGVxLl.png">http://i.imgur.com/1AGVxLl.png</a>) from a
     * single image url (e.g. <a href="http://imgur.com/1AGVxLl">http://imgur.com/1AGVxLl</a>)
     *
     * @param url The single image url.
     * @return The direct image url.
     */
    private static String singleImageUrlToDirectImageUrl(final String url) {
        return HttpUrl.parse(url).newBuilder().host("i.imgur.com").build() + ".png";
    }
}
