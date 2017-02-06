package com.veyndan.paper.reddit.menuitem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public interface MenuItemCommand {

    void execute(AppCompatActivity activity, Bundle bundle);
}
