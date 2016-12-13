package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.paper.reddit.databinding.FragmentTimePeriodFilterBinding;

public class TimePeriodFilterFragment extends Fragment implements Filter {

    private FragmentTimePeriodFilterBinding binding;

    @SuppressWarnings("RedundantNoArgConstructor")
    public TimePeriodFilterFragment() {
        // Required empty public constructor
    }

    public static TimePeriodFilterFragment newInstance() {
        return new TimePeriodFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentTimePeriodFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();
        bundle.putInt(TIME_PERIOD_POSITION, binding.filterFormTimePeriod.getSelectedItemPosition());
        return bundle;
    }
}
