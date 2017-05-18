package com.veyndan.paper.reddit.post

import android.app.Activity

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.post.delegate.MoreAdapterDelegate
import com.veyndan.paper.reddit.post.delegate.PostAdapterDelegate
import com.veyndan.paper.reddit.post.delegate.ProgressAdapterDelegate
import com.veyndan.paper.reddit.util.Node

import retrofit2.Response

class PostAdapter(activity: Activity, posts: MutableList<Node<Response<Thing<Listing>>>>, reddit: Reddit) : ListDelegationAdapter<MutableList<Node<Response<Thing<Listing>>>>>() {

    init {
        delegatesManager
                .addDelegate(PostAdapterDelegate(this, activity, reddit))
                .addDelegate(ProgressAdapterDelegate())
                .addDelegate(MoreAdapterDelegate())

        setItems(posts)
    }
}
