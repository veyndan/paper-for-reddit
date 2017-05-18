package com.veyndan.paper.reddit

import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.util.Node
import retrofit2.Response

data class NextPageUiModel(val forest: List<Node<Response<Thing<Listing>>>>) {

    constructor(tree: Node<Response<Thing<Listing>>>): this(listOf(tree))
}
