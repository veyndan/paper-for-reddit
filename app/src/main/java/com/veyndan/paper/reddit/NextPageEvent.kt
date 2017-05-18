package com.veyndan.paper.reddit

import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.util.Node
import retrofit2.Response

data class NextPageEvent(val tree: Node<Response<Thing<Listing>>>)
