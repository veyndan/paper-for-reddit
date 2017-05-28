package com.veyndan.paper.reddit.post.model

import android.support.annotation.IntRange
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.util.Node
import io.reactivex.Maybe
import io.reactivex.Observable
import retrofit2.Response

data class Progress(@IntRange(from = 0) val degree: Int? = null) : Node<Response<Thing<Listing>>>() {

    override fun degree(): Maybe<Long> {
        return if (degree == null) Maybe.empty() else Maybe.just(degree.toLong())
    }

    override fun children(): Observable<Node<Response<Thing<Listing>>>> {
        return Observable.empty()
    }
}
