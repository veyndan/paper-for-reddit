package com.veyndan.redditclient;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.deeplinkdispatch.DeepLinkDelegate;
import com.airbnb.deeplinkdispatch.DeepLinkHandler;

@DeepLinkHandler
public class CustomDeepLinkHandler extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri standardizedUri = getIntent().getData().buildUpon()
                .scheme("http")
                .authority("reddit.com")
                .build();

        getIntent().setData(standardizedUri);

        DeepLinkDelegate.dispatchFrom(this);
        finish();
    }
}
