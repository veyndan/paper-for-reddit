package com.veyndan.paper.reddit.api.reddit.model;

import android.support.annotation.Nullable;

public class Account2 {
    // Created
    public long created;
    public long createdUtc;

    // Documented
    public int commentKarma;
    public boolean hasMail;
    public boolean hasModMail;
    public boolean hasVerifiedEmail;
    @Nullable public String id;
    public int inboxCount;
    public boolean isFriend;
    public boolean isGold;
    public boolean isMod;
    public int linkKarma;
    @Nullable public String modhash;
    @Nullable public String name;
    public boolean over18;

    // Undocumented
    public boolean isEmployee;
    public boolean hideFromRobots;
    public boolean isSuspended;
    public boolean inBeta;
    @Nullable public Features features;
    @Nullable public Object goldExpiration;
    public int goldCreddits;
    @Nullable public Object suspensionExpirationUtc; // Type not sure
}
