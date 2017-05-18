package com.veyndan.paper.reddit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.databinding.FragmentSubredditFilterBinding

class SubredditFilterFragment : Fragment(), Filter {

    private lateinit var binding: FragmentSubredditFilterBinding

    companion object {

        @JvmStatic
        fun newInstance(): SubredditFilterFragment = SubredditFilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSubredditFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun requestFilter(): Reddit.Filter = Reddit.Filter(
            nodeDepth = 0,
            subredditName = binding.filterFormSubreddit.text.toString())
}
