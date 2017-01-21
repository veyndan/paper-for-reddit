package com.veyndan.paper.reddit.image;

import android.graphics.Bitmap;

import java.io.InputStream;

public interface Decoder {

    Bitmap decodeInputStream(InputStream inputStream);
}
