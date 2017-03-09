package com.veyndan.paper.reddit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.support.design.widget.RxTabLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.veyndan.paper.reddit.databinding.FragmentFilterBinding;

import timber.log.Timber;

public class FilterFragment extends DialogFragment {

    @SuppressWarnings("RedundantNoArgConstructor")
    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final FragmentFilterBinding binding = FragmentFilterBinding.inflate(inflater, container, false);

        final Fragment[] fragments = {
                TimePeriodFilterFragment.newInstance(),
                SubredditFilterFragment.newInstance(),
                UserFilterFragment.newInstance()
        };

        RxView.clicks(binding.filterDone)
                .subscribe(aVoid -> {
                    final Intent intent = new Intent(getContext(), MainActivity.class);
                    for (final Fragment fragment : fragments) {
                        intent.putExtras(((Filter) fragment).requestFilter());
                    }
                    startActivity(intent);

                    dismiss();
                }, Timber::e);

        final FragmentManager fragmentManager = getChildFragmentManager();
        binding.filterViewPager.setAdapter(new FilterSectionAdapter(fragmentManager, fragments));

        binding.filterTabs.setupWithViewPager(binding.filterViewPager);

        TabLayout.Tab tab = binding.filterTabs.getTabAt(0);
        tab.setIcon(R.drawable.ic_schedule_black_24dp);

        tab = binding.filterTabs.getTabAt(1);
        tab.setText("r/");

        tab = binding.filterTabs.getTabAt(2);
        tab.setIcon(R.drawable.ic_person_black_24dp);

        final int colorAccent = ContextCompat.getColor(getActivity(), R.color.colorAccent);

        RxTabLayout.selectionEvents(binding.filterTabs)
                .filter(selectionEvent -> selectionEvent.tab().getIcon() != null)
                .subscribe(selectionEvent -> {
                    final TabLayout.Tab tab1 = selectionEvent.tab();
                    final Drawable icon = tab1.getIcon().mutate();
                    if (tab1.isSelected()) {
                        icon.setColorFilter(colorAccent, PorterDuff.Mode.SRC_IN);
                        icon.setAlpha(255);
                    } else {
                        icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                        icon.setAlpha((int) (0.54 * 255));
                    }
                }, Timber::e);

        return binding.getRoot();
    }

    private static class FilterSectionAdapter extends FragmentStatePagerAdapter {

        private final int tabCount;
        private final Fragment[] fragments;

        FilterSectionAdapter(final FragmentManager fm, final Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;

            tabCount = fragments.length;
        }

        @Override
        public Fragment getItem(final int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
