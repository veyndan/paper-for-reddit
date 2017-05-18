package com.veyndan.paper.reddit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.databinding.FragmentUserFilterBinding

class UserFilterFragment : Fragment(), Filter {

    private lateinit var binding: FragmentUserFilterBinding

    companion object {

        @JvmStatic
        fun newInstance(): UserFilterFragment = UserFilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun requestFilter(): Reddit.Filter = Reddit.Filter(
            nodeDepth = 0,
            userName = binding.filterFormUsername.text.toString(),
            userComments = binding.filterFormComments.isChecked,
            userSubmitted = binding.filterFormSubmitted.isChecked,
            userGilded = binding.filterFormGilded.isChecked
    )
}
