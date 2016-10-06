package com.veyndan.redditclient;

import android.os.Parcel;
import android.os.Parcelable;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.User;
import com.veyndan.redditclient.post.PostsFilter;

import retrofit2.Response;
import rx.Observable;

public class UserFilter implements Parcelable, PostsFilter {

    private final String username;
    private final User user;
    private final QueryBuilder query;

    public UserFilter(final String username, final User user) {
        this(username, user, new QueryBuilder());
    }

    public UserFilter(final String username, final User user, final QueryBuilder query) {
        this.username = username;
        this.user = user;
        this.query = query;
    }

    @Override
    public Observable<Response<Thing<Listing>>> getRequestObservable(final Reddit reddit) {
        return Observable.just(query)
                // TODO If the query has never been initialized, then we want it to pass.
                .filter(query -> !query.build().containsKey("after") || query.build().get("after") != null)
                .flatMap(query -> reddit.user(username, user, query))
                .doOnNext(response -> query.after(response.body().data.after));
    }

    protected UserFilter(final Parcel in) {
        username = in.readString();
        user = (User) in.readValue(User.class.getClassLoader());
        query = (QueryBuilder) in.readValue(QueryBuilder.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(username);
        dest.writeValue(user);
        dest.writeValue(query);
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
