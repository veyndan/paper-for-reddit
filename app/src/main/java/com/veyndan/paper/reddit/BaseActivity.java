package com.veyndan.paper.reddit;

import android.os.Bundle;
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
    protected final void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateNonNull(savedInstanceState == null ? new Bundle() : savedInstanceState);
    }

    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
    }

    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
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
