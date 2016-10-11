package com.veyndan.redditclient;

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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding.support.design.widget.RxTabLayout;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterFragment extends DialogFragment {

    @BindView(R.id.filter_view_pager) ViewPager viewPager;
    @BindView(R.id.filter_tabs) TabLayout tabs;
    @BindView(R.id.filter_done) Button doneButton;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        final Fragment[] fragments = new Fragment[]{
                SubredditFilterFragment.newInstance(),
                UserFilterFragment.newInstance()
        };

        RxView.clicks(doneButton)
                .subscribe(aVoid -> {
                    final Intent intent = new Intent(getContext(), MainActivity.class);
                    for (final Fragment fragment : fragments) {
                        intent.putExtras(((Filter) fragment).requestFilter());
                    }
                    startActivity(intent);

                    dismiss();
                });

        final FragmentManager fragmentManager = getChildFragmentManager();
        viewPager.setAdapter(new FilterSectionAdapter(fragmentManager, fragments));

        tabs.setupWithViewPager(viewPager);

        final int tabCount = tabs.getTabCount();
        for (int i = 1; i < tabCount; i++) {
            final TabLayout.Tab tab = tabs.getTabAt(i);
            tab.setIcon(R.drawable.ic_person_black_24dp);
        }

        final int colorAccent = ContextCompat.getColor(getActivity(), R.color.colorAccent);

        RxTabLayout.selectionEvents(tabs)
                .filter(selectionEvent -> selectionEvent.tab().getIcon() != null)
                .subscribe(selectionEvent -> {
                    final TabLayout.Tab tab = selectionEvent.tab();
                    final Drawable icon = tab.getIcon().mutate();
                    switch (selectionEvent.kind()) {
                        case SELECTED:
                            icon.setColorFilter(colorAccent, PorterDuff.Mode.SRC_IN);
                            icon.setAlpha(255);
                            break;
                        case UNSELECTED:
                            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                            icon.setAlpha((int) (0.54 * 255));
                            break;
                    }
                });

        return view;
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

        @Override
        public CharSequence getPageTitle(final int position) {
            return position == 0 ? "r/" : "";
        }
    }
}
