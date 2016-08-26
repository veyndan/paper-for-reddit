package com.veyndan.redditclient.post.mutator;

import com.veyndan.redditclient.api.reddit.model.Link;
import com.veyndan.redditclient.api.reddit.model.PostHint;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Submission;

import okhttp3.HttpUrl;

class ImgurMutatorFactory implements MutatorFactory {

    static ImgurMutatorFactory create() {
        return new ImgurMutatorFactory();
    }

    private ImgurMutatorFactory() {
    }

    @Override
    public boolean applicable(final RedditObject post) {
        final Submission submission = (Submission) post;
        final String urlHost = HttpUrl.parse(submission.linkUrl).host();
        return submission instanceof Link &&
                urlHost.equals("imgur.com") || urlHost.equals("i.imgur.com");
    }

    @Override
    public void mutate(final RedditObject post) {
        final Submission submission = (Submission) post;
        if (!isAlbum(submission.linkUrl) && !isDirectImage(submission.linkUrl)) {
            // TODO .gifv links are HTML 5 videos so the PostHint should be set accordingly.
            if (!submission.linkUrl.endsWith(".gifv")) {
                submission.linkUrl = singleImageUrlToDirectImageUrl(submission.linkUrl);
                ((Link) submission).setPostHint(PostHint.IMAGE);
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
