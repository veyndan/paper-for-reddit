package com.veyndan.paper.reddit

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.design.widget.selectionEvents
import com.jakewharton.rxbinding2.view.clicks
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.databinding.FragmentFilterBinding

class FilterFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentFilterBinding = FragmentFilterBinding.inflate(inflater, container, false)

        val fragments: Array<Fragment> = arrayOf(TimePeriodFilterFragment.newInstance(), SubredditFilterFragment.newInstance(), UserFilterFragment.newInstance())

        binding.filterDone.clicks()
                .subscribe {
                    val intent: Intent = Intent(context, MainActivity::class.java)
                    for (fragment in fragments) {
                        intent.putExtra(Reddit.FILTER, (fragment as Filter).requestFilter())
                    }
                    startActivity(intent)

                    dismiss()
                }

        val fragmentManager: FragmentManager = childFragmentManager
        binding.filterViewPager.adapter = FilterSectionAdapter(fragmentManager, fragments)

        binding.filterTabs.setupWithViewPager(binding.filterViewPager)

        var tab: TabLayout.Tab = binding.filterTabs.getTabAt(0)!!
        tab.setIcon(R.drawable.ic_schedule_black_24dp)

        tab = binding.filterTabs.getTabAt(1)!!
        tab.text = "r/"

        tab = binding.filterTabs.getTabAt(2)!!
        tab.setIcon(R.drawable.ic_person_black_24dp)

        @ColorInt val colorAccent: Int = ContextCompat.getColor(activity, R.color.colorAccent)

        binding.filterTabs.selectionEvents()
                .filter { selectionEvent -> selectionEvent.tab().icon != null }
                .subscribe { selectionEvent ->
                    val tab1: TabLayout.Tab = selectionEvent.tab()
                    val icon: Drawable = tab1.icon!!.mutate()
                    if (tab1.isSelected) {
                        icon.setColorFilter(colorAccent, PorterDuff.Mode.SRC_IN)
                        icon.alpha = 255
                    } else {
                        icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
                        icon.alpha = (0.54 * 255).toInt()
                    }
                }

        return binding.root
    }

    private class FilterSectionAdapter internal constructor(fm: FragmentManager, private val fragments: Array<Fragment>) : FragmentStatePagerAdapter(fm) {

        private val tabCount: Int = fragments.size

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return tabCount
        }
    }

    companion object {

        fun newInstance(): FilterFragment {
            return FilterFragment()
        }
    }
}
