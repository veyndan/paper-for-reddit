package com.veyndan.redditclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentsFragment extends Fragment {

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }
}
