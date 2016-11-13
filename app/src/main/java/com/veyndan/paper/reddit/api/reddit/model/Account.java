package com.veyndan.paper.reddit.api.reddit.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Account {

    public abstract long date();

    public abstract String name();

    public abstract String id();

    public static TypeAdapter<Account> typeAdapter(final Gson gson) {
        return new AutoValue_Account.GsonTypeAdapter(gson);
    }
}
