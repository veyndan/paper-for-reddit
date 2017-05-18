package com.veyndan.paper.reddit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.api.reddit.network.TimePeriod
import com.veyndan.paper.reddit.databinding.FragmentTimePeriodFilterBinding

class TimePeriodFilterFragment : Fragment(), Filter {

    private lateinit var binding: FragmentTimePeriodFilterBinding

    companion object {

        @JvmStatic
        fun newInstance(): TimePeriodFilterFragment = TimePeriodFilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimePeriodFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun requestFilter(): Reddit.Filter = Reddit.Filter(
            nodeDepth = 0,
            timePeriod = TimePeriod.values()[binding.filterFormTimePeriod.selectedItemPosition])
}
