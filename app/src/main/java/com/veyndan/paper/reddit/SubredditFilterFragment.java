package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.databinding.FragmentSubredditFilterBinding;

public class SubredditFilterFragment extends Fragment implements Filter {

    private FragmentSubredditFilterBinding binding;

    @SuppressWarnings("RedundantNoArgConstructor")
    public SubredditFilterFragment() {
        // Required empty public constructor
    }

    public static SubredditFilterFragment newInstance() {
        return new SubredditFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentSubredditFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public Bundle requestFilter() {
        return new Reddit.FilterBuilder()
                .nodeDepth(0)
                .subredditName(binding.filterFormSubreddit.getText().toString())
                .build();
    }
}
