package com.veyndan.redditclient.api.model;

public class Account2 {
    // Created
    public long created;
    public long createdUtc;

    // Documented
    public int commentKarma;
    public boolean hasMail;
    public boolean hasModMail;
    public boolean hasVerifiedEmail;
    public String id;
    public int inboxCount;
    public boolean isFriend;
    public boolean isGold;
    public boolean isMod;
    public int linkKarma;
    public String modhash;
    public String name;
    public boolean over18;

    // Undocumented
    public boolean isEmployee;
    public boolean hideFromRobots;
    public boolean isSuspended;
    public boolean inBeta;
    public Features features;
    public Object goldExpiration;
    public int goldCreddits;
    public Object suspensionExpirationUtc; // Type not sure
}
