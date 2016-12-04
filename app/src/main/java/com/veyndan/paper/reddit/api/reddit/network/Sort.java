package com.veyndan.paper.reddit.api.reddit.network;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum Sort {
    @SerializedName("hot")HOT,
    @SerializedName("new")NEW,
    @SerializedName("rising")RISING,
    @SerializedName("controversial")CONTROVERSIAL,
    @SerializedName("top")TOP;

    @NonNull
    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
