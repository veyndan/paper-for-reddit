package com.veyndan.paper.reddit.api.reddit.model

import com.veyndan.paper.reddit.api.reddit.network.VoteDirection

interface Votable {

    val ups: Int

    val downs: Int

    val likes: VoteDirection
}
