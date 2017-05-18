package com.veyndan.paper.reddit

import com.veyndan.paper.reddit.api.reddit.Reddit

interface Filter {

    fun requestFilter(): Reddit.Filter
}
