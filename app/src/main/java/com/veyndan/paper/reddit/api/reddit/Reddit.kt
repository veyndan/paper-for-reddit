package com.veyndan.paper.reddit.api.reddit

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntRange
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.veyndan.paper.reddit.BuildConfig
import com.veyndan.paper.reddit.api.reddit.json.adapter.DefaultOnDataMismatchAdapter
import com.veyndan.paper.reddit.api.reddit.json.adapter.RedditObjectAdapter
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Submission
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.api.reddit.network.*
import com.veyndan.paper.reddit.api.reddit.network.interceptor.AccessTokenInterceptor
import com.veyndan.paper.reddit.api.reddit.network.interceptor.AuthorizationInterceptor
import com.veyndan.paper.reddit.api.reddit.network.interceptor.RawJsonInterceptor
import com.veyndan.paper.reddit.api.reddit.network.interceptor.UserAgentInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class Reddit(credentials: Credentials) {

    companion object {

        @JvmField val FILTER: String = "filter"
    }

    private val redditService: RedditService

    init {
        val moshi: Moshi = Moshi.Builder()
                .add(RedditObjectAdapter.FACTORY)
                // According to the Reddit json contract, if there are no replies an empty string
                // is returned instead of an empty object or null as expected. This sets an empty
                // object if there are no replies.
                .add(DefaultOnDataMismatchAdapter.newFactory(Types.newParameterizedType(Thing::class.java, Listing::class.java), Thing(Listing())))
                .build()

        val jsonConverterFactory: MoshiConverterFactory = MoshiConverterFactory.create(moshi)

        val rxJava2CallAdapterFactory: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

        val client: OkHttpClient = OkHttpClient()

        val authenticationClientBuilder: OkHttpClient.Builder = client.newBuilder()
                .addInterceptor(UserAgentInterceptor(credentials.userAgent))
                .addInterceptor(AuthorizationInterceptor(credentials))

        val authenticatorRetrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(authenticationClientBuilder.build())
                .build()

        val authenticationService: AuthenticationService = authenticatorRetrofit.create(AuthenticationService::class.java)

        val clientBuilder: OkHttpClient.Builder = client.newBuilder()
                .addInterceptor(UserAgentInterceptor(credentials.userAgent))
                .addInterceptor(AccessTokenInterceptor(authenticationService, credentials))
                .addInterceptor(RawJsonInterceptor())

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor())
        }

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://oauth.reddit.com/")
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(jsonConverterFactory)
                .client(clientBuilder.build())
                .build()

        redditService = retrofit.create(RedditService::class.java)
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        return loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // ================================
    //         Links & Comments
    // ================================

    fun hide(vararg ids: String): Single<Response<Void>> = hide(ids.toList())

    fun hide(ids: List<String>): Single<Response<Void>> = redditService.hide(ids.joinToString(","))

    fun save(category: String, id: String): Single<Response<Void>> = redditService.save(category, id)

    fun unsave(id: String): Single<Response<Void>> = redditService.unsave(id)

    fun vote(voteDirection: VoteDirection, id: String): Single<Response<Void>> = redditService.vote(voteDirection, id)

    // ================================
    //             Listings
    // ================================

    private fun subredditComments(subreddit: String, article: String): Single<Response<Thing<Listing>>> =
            redditService.subredditComments(subreddit, article)
                    .map { response ->
                        val things: List <Thing <Listing>> = response.body()
                        (things[0].data.children[0] as Submission).replies.data.children.addAll(things[1].data.children)
                        Response.success(things[0])
                    }

    private fun subreddit(subreddit: String, sort: Sort, timePeriod: TimePeriod?): Single<Response<Thing<Listing>>> {
        val query: QueryBuilder = QueryBuilder()

        if (timePeriod != null) {
            query.t(timePeriod)
        }

        val request: Single<Response<Thing<Listing>>> = redditService.subreddit(subreddit, sort, query.build())
        return paginate(request, query)
    }

    // ================================
    //              Search
    // ================================

    private fun search(subreddit: String, searchQuery: String): Single<Response<Thing<Listing>>> {
        val query: QueryBuilder = QueryBuilder()
                .q(searchQuery)

        return redditService.search(subreddit, query.build())
    }

    // ================================
    //              Users
    // ================================

    private fun user(username: String, where: User, timePeriod: TimePeriod?): Single<Response<Thing<Listing>>> {
        val query: QueryBuilder = QueryBuilder()

        if (timePeriod != null) {
            query.t(timePeriod)
        }

        val request: Single<Response<Thing<Listing>>> = redditService.user(username, where, query.build())
        return paginate(request, query)
    }

    data class Filter(
            /**
             * If the nodeDepth() == Integer.MAX_VALUE, this is synonymous with nodeDepth() being
             * infinite. This means that every node should be retrieved.
             */
            @IntRange(from = 0) val nodeDepth: Int = Integer.MAX_VALUE,

            val commentsSubreddit: String = "",
            val commentsArticle: String = "",
            val timePeriod: TimePeriod? = null,
            val subredditName: String = "",
            val searchQuery: String = "",
            val userName: String = "",
            val userComments: Boolean = false,
            val userSubmitted: Boolean = false,
            val userGilded: Boolean = false
    ) : Parcelable {
        init {
            check(nodeDepth >= 0) { "nodeDepth must be non negative" }
        }

        companion object {
            @JvmField val CREATOR: Parcelable.Creator<Filter> = object : Parcelable.Creator<Filter> {
                override fun createFromParcel(source: Parcel): Filter = Filter(source)
                override fun newArray(size: Int): Array<Filter?> = arrayOfNulls(size)
            }
        }

        constructor(source: Parcel) : this(
                source.readInt(),
                source.readString(),
                source.readString(),
                source.readSerializable() as TimePeriod,
                source.readString(),
                source.readString(),
                source.readString(),
                1 == source.readInt(),
                1 == source.readInt(),
                1 == source.readInt()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(nodeDepth)
            dest.writeString(commentsSubreddit)
            dest.writeString(commentsArticle)
            dest.writeSerializable(timePeriod)
            dest.writeString(subredditName)
            dest.writeString(searchQuery)
            dest.writeString(userName)
            dest.writeInt((if (userComments) 1 else 0))
            dest.writeInt((if (userSubmitted) 1 else 0))
            dest.writeInt((if (userGilded) 1 else 0))
        }
    }

    fun query(filter: Filter, sort: Sort): Single<Response<Thing<Listing>>> {
        when (filter.nodeDepth) {
            0 -> {
                if (filter.commentsSubreddit.isEmpty() && filter.commentsArticle.isEmpty()
                        && filter.searchQuery.isEmpty()
                        && filter.userName.isEmpty() && !filter.userComments && !filter.userSubmitted && !filter.userGilded) {
                    return subreddit(filter.subredditName ?: "all", sort, filter.timePeriod ?: TimePeriod.ALL)
                }

                if (filter.commentsSubreddit.isEmpty() && filter.commentsArticle.isEmpty()
                        && filter.subredditName.isEmpty()
                        && filter.searchQuery.isEmpty()
                        && filter.userName.length > 1) {
                    val userWhere: User = toUser(filter.userComments, filter.userSubmitted, filter.userGilded)
                    return user(filter.userName, userWhere, filter.timePeriod)
                }

                if (filter.commentsSubreddit.isEmpty() && filter.commentsArticle.isEmpty()
                        && filter.timePeriod == null
                        && filter.subredditName.isNotEmpty()
                        && filter.searchQuery.isNotEmpty()
                        && filter.userName.isEmpty() && !filter.userComments && !filter.userSubmitted && !filter.userGilded) {
                    return search(filter.subredditName, filter.searchQuery)
                }

                if (filter.commentsSubreddit.length > 1 && filter.commentsArticle.length > 1
                        && filter.timePeriod == null
                        && filter.subredditName.isEmpty()
                        && filter.searchQuery.isEmpty()
                        && filter.userName.isEmpty() && !filter.userComments && !filter.userSubmitted && !filter.userGilded) {
                    return subredditComments(filter.commentsSubreddit, filter.commentsArticle)
                }

                throw IllegalStateException()
            }
            in 1..(Integer.MAX_VALUE - 1) -> throw IllegalStateException("Depths that aren't 0 or Integer.MAX_VALUE aren't available to the Reddit API")
            Integer.MAX_VALUE -> {
                // TODO Get every post, along with its comments here i.e. the whole forest of Reddit
                throw IllegalStateException()
            }
            else -> throw IllegalStateException()
        }
    }

    private fun toUser(comments: Boolean, submitted: Boolean, gilded: Boolean): User = when {
        comments == submitted && gilded -> User.GILDED
        comments != submitted && gilded -> throw UnsupportedOperationException("User state unsure")
        comments && submitted -> User.OVERVIEW
        comments -> User.COMMENTS
        submitted -> User.SUBMITTED
        else -> User.OVERVIEW
    }

    private fun paginate(page: Single<Response<Thing<Listing>>>, query: QueryBuilder): Single<Response<Thing<Listing>>> =
            Single.just(query)
                    // TODO If the query has never been initialized, then we want it to pass.
                    .filter { !it.build().containsKey("after") || it.build()["after"] != null }
                    .flatMapSingle { page }
                    .doOnSuccess { response -> query.after(response.body().data.after!!) }
}
