package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

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
    }

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        final ActionBar ab = super.getSupportActionBar();
        return Objects.requireNonNull(ab, "An ActionBar should be attached to this activity");
    }
}
