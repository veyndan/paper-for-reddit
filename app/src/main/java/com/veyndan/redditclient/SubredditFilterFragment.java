package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubredditFilterFragment extends Fragment implements Filter {

    @BindView(R.id.filter_form_subreddit) EditText formSubreddit;

    public SubredditFilterFragment() {
        // Required empty public constructor
    }

    public static SubredditFilterFragment newInstance() {
        return new SubredditFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_subreddit_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();

        final String subreddit = formSubreddit.getText().toString();
        bundle.putString("subreddit", subreddit);

        return bundle;
    }
}
