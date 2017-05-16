package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Account {

    public abstract long date();

    public abstract String name();

    public abstract String id();

    public static JsonAdapter<Account> jsonAdapter(final Moshi moshi) {
        return new AutoValue_Account.MoshiJsonAdapter(moshi);
    }
}
