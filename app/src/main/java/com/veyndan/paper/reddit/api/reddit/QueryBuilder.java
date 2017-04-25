package com.veyndan.paper.reddit.api.reddit;

import android.support.annotation.IntRange;
import android.support.annotation.Size;

import com.veyndan.paper.reddit.api.reddit.network.TimePeriod;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Convenience class to create a {@link Map map} of queries.
 * <p>
 * Note: No checking is done to ensure that the queries requested by this class will be used by the
 * the API endpoint. E.g. specifying the {@link TimePeriod time period} in {@link #t(TimePeriod) t}
 * when the API endpoint is {@code https://oauth.reddit.com[/r/subreddit]/new} wouldn't make sense.
 * <p>
 * To see which queries are excepted by each endpoint, see
 * <a href="https://www.reddit.com/dev/api">https://www.reddit.com/dev/api</a>.
 */
class QueryBuilder {

    private final Map<String, String> query;

    public QueryBuilder() {
        query = new HashMap<>();

        // restrict_sr="off" is equivalent to the subreddit being /r/all.
        query.put("restrict_sr", "on");
    }

    /**
     * Indicates the {@code fullname} of an item in the listing to use as the anchor point of the
     * slice.
     * <p>
     * Note: If this query is used then {@link #before(String) before} shouldn't be specified.
     *
     * @param fullname fullname of a thing
     */
    public QueryBuilder after(final String fullname) {
        query.put("after", fullname);
        return this;
    }

    /**
     * Indicates the {@code fullname} of an item in the listing to use as the anchor point of the
     * slice.
     * <p>
     * Note: If this query is used then {@link #after(String) after} shouldn't be specified.
     *
     * @param fullname fullname of a thing
     */
    public QueryBuilder before(final String fullname) {
        query.put("before", fullname);
        return this;
    }

    /**
     * The number of items already seen in this listing. on the html site, the builder uses this to
     * determine when to give values for {@link #before(String) before} and
     * {@link #after(String) after} in the response.
     *
     * @param count A positive integer (default: 0)
     */
    public QueryBuilder count(@IntRange(from = 0) final int count) {
        checkArgument(count >= 0, "Query parameter 'count' must be non negative.");

        query.put("count", String.valueOf(count));
        return this;
    }

    /**
     * The maximum number of items to return in this slice of the listing.
     *
     * @param limit The maximum number of items desired (default: 25, maximum: 100)
     */
    public QueryBuilder limit(@IntRange(from = 0, to = 100) final int limit) {
        checkArgument(limit >= 0 && limit <= 100, "Query parameter 'limit' must be between 0 and 100");

        query.put("limit", String.valueOf(limit));
        return this;
    }

    /**
     * @param all If {@code true}, filters such as "hide links that I have voted on" will be
     *            disabled.
     */
    public QueryBuilder show(final boolean all) {
        if (all) {
            query.put("show", "all");
        } else {
            query.remove("show");
        }
        return this;
    }

    /**
     * The time period in which the query should span e.g. sorting a subreddit by top posts in the
     * last hour, last 24 hours etc.
     * <p>
     * #inferred
     */
    public QueryBuilder t(final TimePeriod timePeriod) {
        query.put("t", timePeriod.toString());
        return this;
    }

    /**
     * The search query for the request. Must be no longer than 512 characters.
     * <p>
     * #inferred
     */
    public QueryBuilder q(@Size(max = 512) final String searchQuery) {
        checkArgument(searchQuery.length() <= 512, "Query parameter 'q' must be no longer than " +
                "512 characters.");

        query.put("q", searchQuery);
        return this;
    }

    /**
     * #undocumented
     */
    public QueryBuilder srDetail(final Object expandSubreddits) {
        query.put("sr_detail", String.valueOf(expandSubreddits));
        return this;
    }

    public Map<String, String> build() {
        return query;
    }
}
