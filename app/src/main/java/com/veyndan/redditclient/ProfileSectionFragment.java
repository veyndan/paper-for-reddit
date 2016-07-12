package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileSectionFragment extends Fragment {

    public ProfileSectionFragment() {
        // Required empty public constructor
    }

    public static ProfileSectionFragment newInstance() {
        return new ProfileSectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_section_fragment, container, false);
    }
}
