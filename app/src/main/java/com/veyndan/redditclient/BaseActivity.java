package com.veyndan.redditclient;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        final ActionBar ab = super.getSupportActionBar();
        return Objects.requireNonNull(ab, "An ActionBar should be attached to this activity");
    }
}
