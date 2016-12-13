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
        final Bundle bundle = new Bundle();

        bundle.putInt(Reddit.Filter.NODE_DEPTH, 0);

        final String username = binding.filterFormUsername.getText().toString();
        bundle.putString(Reddit.Filter.USER_NAME, username);

        final boolean comments = binding.filterFormComments.isChecked();
        bundle.putBoolean(Reddit.Filter.USER_COMMENTS, comments);

        final boolean submitted = binding.filterFormSubmitted.isChecked();
        bundle.putBoolean(Reddit.Filter.USER_SUBMITTED, submitted);

        final boolean gilded = binding.filterFormGilded.isChecked();
        bundle.putBoolean(Reddit.Filter.USER_GILDED, gilded);

        return bundle;
    }
}
