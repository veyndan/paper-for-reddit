package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.network.TimePeriod;
import com.veyndan.paper.reddit.databinding.FragmentTimePeriodFilterBinding;

public class TimePeriodFilterFragment extends Fragment implements Filter {

    private FragmentTimePeriodFilterBinding binding;

    @SuppressWarnings("RedundantNoArgConstructor")
    public TimePeriodFilterFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static TimePeriodFilterFragment newInstance() {
        return new TimePeriodFilterFragment();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentTimePeriodFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();

        final TimePeriod[] timePeriods = {
                TimePeriod.HOUR,
                TimePeriod.DAY,
                TimePeriod.WEEK,
                TimePeriod.MONTH,
                TimePeriod.YEAR,
                TimePeriod.ALL
        };

        bundle.putInt(Reddit.Filter.NODE_DEPTH, 0);
        bundle.putSerializable(Reddit.Filter.TIME_PERIOD, timePeriods[binding.filterFormTimePeriod.getSelectedItemPosition()]);
        return bundle;
    }
}
