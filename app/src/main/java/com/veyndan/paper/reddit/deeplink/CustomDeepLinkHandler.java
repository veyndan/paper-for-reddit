package com.veyndan.paper.reddit.deeplink;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.deeplinkdispatch.DeepLinkHandler;

@DeepLinkHandler(AppDeepLinkModule.class)
public class CustomDeepLinkHandler extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DeepLinkDelegate deepLinkDelegate = new DeepLinkDelegate(new AppDeepLinkModuleLoader());

        deepLinkDelegate.dispatchFrom(this);

        finish();
    }
}
