package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFilterFragment extends Fragment implements Filter {

    @BindView(R.id.filter_form_username) EditText formUsernameEditText;
    @BindView(R.id.filter_form_comments) CheckBox formCommentsCheckBox;
    @BindView(R.id.filter_form_submitted) CheckBox formSubmittedCheckBox;
    @BindView(R.id.filter_form_gilded) CheckBox formGildedCheckBox;

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
        bundle.putString(USER_NAME, username);

        final boolean comments = formCommentsCheckBox.isChecked();
        bundle.putBoolean(USER_COMMENTS, comments);

        final boolean submitted = formSubmittedCheckBox.isChecked();
        bundle.putBoolean(USER_SUBMITTED, submitted);

        final boolean gilded = formGildedCheckBox.isChecked();
        bundle.putBoolean(USER_GILDED, gilded);

        return bundle;
    }
}
