package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterFragment extends DialogFragment {

    @BindView(R.id.filter_form_username) EditText formUsernameEditText;
    @BindView(R.id.filter_done) Button doneButton;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        return new FilterFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        RxView.clicks(doneButton)
                .subscribe(aVoid -> {
                    final Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("username", formUsernameEditText.getText().toString());
                    startActivity(intent);
                    dismiss();
                });

        return view;
    }
}
