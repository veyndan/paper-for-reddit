package com.veyndan.redditclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.veyndan.redditclient.api.reddit.network.User;

public class UserFilter implements Parcelable {

    private final String username;
    private final User user;

    public UserFilter(final String username, final User user) {
        this.username = username;
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public User getUser() {
        return user;
    }

    protected UserFilter(final Parcel in) {
        username = in.readString();
        user = (User) in.readValue(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(username);
        dest.writeValue(user);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserFilter> CREATOR = new Parcelable.Creator<UserFilter>() {
        @Override
        public UserFilter createFromParcel(final Parcel in) {
            return new UserFilter(in);
        }

        @Override
        public UserFilter[] newArray(final int size) {
            return new UserFilter[size];
        }
    };
}
