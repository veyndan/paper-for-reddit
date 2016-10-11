package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFilterFragment extends Fragment implements Filter {

    @BindView(R.id.filter_form_username) EditText formUsernameEditText;

    public UserFilterFragment() {
        // Required empty public constructor
    }

    public static UserFilterFragment newInstance() {
        return new UserFilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Bundle requestFilter() {
        final Bundle bundle = new Bundle();
        final String username = formUsernameEditText.getText().toString();
        bundle.putString("username", username);
        return bundle;
    }
}
