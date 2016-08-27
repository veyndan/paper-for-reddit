package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.post.model.Post;

import okhttp3.HttpUrl;

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
                ((Link) post.submission).setPostHint(PostHint.IMAGE);
            }
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
