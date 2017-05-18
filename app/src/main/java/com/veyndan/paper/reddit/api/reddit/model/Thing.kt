package com.veyndan.paper.reddit.api.reddit.model

data class Thing<T> @JvmOverloads constructor(val data: T, val kind: Kind? = null)
