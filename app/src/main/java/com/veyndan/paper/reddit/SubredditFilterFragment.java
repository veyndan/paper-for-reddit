package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @NonNull
    public static SubredditFilterFragment newInstance() {
        return new SubredditFilterFragment();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentSubredditFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();

        bundle.putInt(Reddit.Filter.NODE_DEPTH, 0);

        final String subreddit = binding.filterFormSubreddit.getText().toString();
        bundle.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

        return bundle;
    }
}
