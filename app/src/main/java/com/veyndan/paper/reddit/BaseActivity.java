package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;

import com.trello.navi2.component.support.NaviAppCompatActivity;

import java.util.Objects;

public abstract class BaseActivity extends NaviAppCompatActivity {

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        final ActionBar ab = super.getSupportActionBar();
        return Objects.requireNonNull(ab, "An ActionBar should be attached to this activity");
    }
}
