package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;

import com.trello.navi2.component.support.NaviAppCompatActivity;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseActivity extends NaviAppCompatActivity {

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        final ActionBar ab = super.getSupportActionBar();
        return checkNotNull(ab, "An ActionBar should be attached to this activity");
    }
}
