package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

// t5_
public class Subreddit {
    @Nullable public String bannerImg;
    @Nullable public Boolean userSrThemeEnabled;
    @Nullable public Object submitTextHtml;
    @Nullable public Boolean userIsBanned;
    @Nullable public Boolean wikiEnabled;
    @Nullable public String id;
    @Nullable public String submitText;
    @Nullable public String displayName;
    @Nullable public String headerImg;
    @Nullable public String descriptionHtml;
    @Nullable public String title;
    @Nullable public Boolean collapseDeletedComments;
    @Nullable public Boolean over18;
    @Nullable public String publicDescriptionHtml;
    @NonNull public List<Integer> iconSize = new ArrayList<>();
    @Nullable public Object suggestedCommentSort;
    @Nullable public String iconImg;
    @Nullable public String headerTitle;
    @Nullable public String description;
    @Nullable public Boolean userIsMuted;
    @Nullable public Object submitLinkLabel;
    @Nullable public Object accountsActive;
    @Nullable public Boolean publicTraffic;
    @NonNull public List<Integer> headerSize = new ArrayList<>();
    @Nullable public Integer subscribers;
    @Nullable public Object submitTextLabel;
    @Nullable public String lang;
    @Nullable public Boolean userIsModerator;
    @Nullable public String keyColor;
    @Nullable public String name;
    @Nullable public Integer created;
    @Nullable public String url;
    @Nullable public Boolean quarantine;
    @Nullable public Boolean hideAds;
    @Nullable public Integer createdUtc;
    @NonNull public List<Integer> bannerSize = new ArrayList<>();
    @Nullable public Boolean userIsContributor;
    @Nullable public String publicDescription;
    @Nullable public Integer commentScoreHideMins;
    @Nullable public String subredditType;
    @Nullable public String submissionType;
    @Nullable public Boolean userIsSubscriber;
}
