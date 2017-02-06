package com.veyndan.paper.reddit.menuitem;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.menuitem.sort.SortHotMenuItemCommand;

import java.util.HashMap;
import java.util.Map;

public class MenuItemCommandRequestHandler {

    private final Map<Integer, MenuItemCommand> commandMap = new HashMap<>();

    public MenuItemCommandRequestHandler() {
        commandMap.put(R.id.action_sort_hot, new SortHotMenuItemCommand());
    }

    public boolean handleRequest(@IdRes final int itemId, final AppCompatActivity activity, final Bundle bundle) {
        final MenuItemCommand command = commandMap.get(itemId);
        if (command != null) {
            command.execute(activity, bundle);
            return true;
        }
        return false;
    }
}
