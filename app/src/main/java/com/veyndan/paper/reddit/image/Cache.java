package com.veyndan.paper.reddit.image;

import android.graphics.Bitmap;

public interface Cache {

    Bitmap get(String key);

    void set(String key, Bitmap bitmap);
}
