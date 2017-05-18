package com.veyndan.paper.reddit

import android.support.v7.app.ActionBar
import com.trello.navi2.component.support.NaviAppCompatActivity

abstract class BaseActivity : NaviAppCompatActivity() {

    override fun getSupportActionBar(): ActionBar {
        val ab: ActionBar? = super.getSupportActionBar()
        return checkNotNull(ab) { "An ActionBar should be attached to this activity" }
    }
}
