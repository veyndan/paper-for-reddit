package com.veyndan.paper.reddit.api.reddit

import android.support.annotation.IntRange
import android.support.annotation.Size
import com.veyndan.paper.reddit.api.reddit.network.TimePeriod

/**
 * Convenience class to create a [map][Map] of queries.
 *
 *
 * Note: No checking is done to ensure that the queries requested by this class will be used by the
 * the API endpoint. E.g. specifying the [time period][TimePeriod] in [t][.t]
 * when the API endpoint is `https://oauth.reddit.com[/r/subreddit]/new` wouldn't make sense.
 *
 *
 * To see which queries are excepted by each endpoint, see
 * [https://www.reddit.com/dev/api](https://www.reddit.com/dev/api).
 */
class QueryBuilder {

    // restrict_sr="off" is equivalent to the subreddit being /r/all.
    private val query: MutableMap<String, String> = hashMapOf("restrict_sr" to "on")

    /**
     * Indicates the `fullname` of an item in the listing to use as the anchor point of the
     * slice.
     *
     * Note: If this query is used then [before][.before] shouldn't be specified.
     *
     * @param fullname fullname of a thing
     */
    fun after(fullname: String): QueryBuilder {
        query.put("after", fullname)
        return this
    }

    /**
     * Indicates the `fullname` of an item in the listing to use as the anchor point of the
     * slice.
     *
     * Note: If this query is used then [after][.after] shouldn't be specified.
     *
     * @param fullname fullname of a thing
     */
    fun before(fullname: String): QueryBuilder {
        query.put("before", fullname)
        return this
    }

    /**
     * The number of items already seen in this listing. on the html site, the builder uses this to
     * determine when to give values for [before][.before] and
     * [after][.after] in the response.
     *
     * @param count A positive integer (default: 0)
     */
    fun count(@IntRange(from = 0) count: Int): QueryBuilder {
        check(count >= 0) { "Query parameter 'count' must be non negative." }

        query.put("count", count.toString())
        return this
    }

    /**
     * The maximum number of items to return in this slice of the listing.
     *
     * @param limit The maximum number of items desired (default: 25, maximum: 100)
     */
    fun limit(@IntRange(from = 0, to = 100) limit: Int): QueryBuilder {
        check(limit in 0..100) { "Query parameter 'limit' must be between 0 and 100" }

        query.put("limit", limit.toString())
        return this
    }

    /**
     * @param all If `true`, filters such as "hide links that I have voted on" will be disabled.
     */
    fun show(all: Boolean): QueryBuilder {
        if (all) {
            query.put("show", "all")
        } else {
            query.remove("show")
        }
        return this
    }

    /**
     * The time period in which the query should span e.g. sorting a subreddit by top posts in the
     * last hour, last 24 hours etc.
     *
     * #inferred
     */
    fun t(timePeriod: TimePeriod): QueryBuilder {
        query.put("t", timePeriod.toString())
        return this
    }

    /**
     * The search query for the request. Must be no longer than 512 characters.
     *
     * #inferred
     */
    fun q(@Size(max = 512) searchQuery: String): QueryBuilder {
        check(searchQuery.length <= 512) { "Query parameter 'q' must be no longer than 512 characters." }

        query.put("q", searchQuery)
        return this
    }

    /**
     * #undocumented
     */
    fun srDetail(expandSubreddits: Any): QueryBuilder {
        query.put("sr_detail", expandSubreddits.toString())
        return this
    }

    fun build(): Map<String, String> = query
}
