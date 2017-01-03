package com.veyndan.paper.reddit.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RedditAuthenticatorService extends Service {

    @Override
    public IBinder onBind(final Intent intent) {
        final AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
