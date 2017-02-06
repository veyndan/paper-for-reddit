package com.veyndan.paper.reddit.menuitem.sort;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.veyndan.paper.reddit.AuthenticationActivity;
import com.veyndan.paper.reddit.menuitem.MenuItemCommand;

public class SortHotMenuItemCommand implements MenuItemCommand {

    @Override
    public void execute(final AppCompatActivity activity, final Bundle bundle) {
        final Intent intent = new Intent(activity, AuthenticationActivity.class);
        activity.startActivityForResult(intent, 0);
    }
}
