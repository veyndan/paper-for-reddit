package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.databinding.FragmentUserFilterBinding;

public class UserFilterFragment extends Fragment implements Filter {

    private FragmentUserFilterBinding binding;

    @SuppressWarnings("RedundantNoArgConstructor")
    public UserFilterFragment() {
        // Required empty public constructor
    }

    public static UserFilterFragment newInstance() {
        return new UserFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentUserFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public Bundle requestFilter() {
        return new Reddit.FilterBuilder()
                .nodeDepth(0)
                .userName(binding.filterFormUsername.getText().toString())
                .userComments(binding.filterFormComments.isChecked())
                .userSubmitted(binding.filterFormSubmitted.isChecked())
                .userGilded(binding.filterFormGilded.isChecked())
                .build();
    }
}
