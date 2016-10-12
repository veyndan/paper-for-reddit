package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimePeriodFilterFragment extends Fragment implements Filter {

    @BindView(R.id.filter_form_time_period) Spinner formTimePeriod;

    public TimePeriodFilterFragment() {
        // Required empty public constructor
    }

    public static TimePeriodFilterFragment newInstance() {
        return new TimePeriodFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_period_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();
        bundle.putInt(TIME_PERIOD_POSITION, formTimePeriod.getSelectedItemPosition());
        return bundle;
    }
}
