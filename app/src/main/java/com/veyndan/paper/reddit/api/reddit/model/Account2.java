package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Account2 {

    // Created
    public abstract long created();

    public abstract long createdUtc();

    // Documented
    public abstract int commentKarma();

    public abstract boolean hasMail();

    public abstract boolean hasModMail();

    public abstract boolean hasVerifiedEmail();

    public abstract String id();

    public abstract int inboxCount();

    public abstract boolean isFriend();

    public abstract boolean isGold();

    public abstract boolean isMod();

    public abstract int linkKarma();

    public abstract String modhash();

    public abstract String name();

    public abstract boolean over18();

    // Undocumented
    public abstract boolean isEmployee();

    public abstract boolean hideFromRobots();

    public abstract boolean isSuspended();

    public abstract boolean inBeta();

    public abstract Features features();

    public abstract Object goldExpiration();

    public abstract int goldCreddits();

    public abstract Object suspensionExpirationUtc(); // Type not sure

    public static TypeAdapter<Account2> typeAdapter(final Gson gson) {
        return new AutoValue_Account2.GsonTypeAdapter(gson);
    }
}
