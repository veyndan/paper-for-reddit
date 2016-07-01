package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rawjava.Reddit;
import rawjava.network.Credentials;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Credentials credentials = Credentials.create(getResources().openRawResource(R.raw.credentials));
        Reddit reddit = new Reddit(credentials);
    }
}
