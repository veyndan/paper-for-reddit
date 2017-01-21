package com.veyndan.paper.reddit.image.imp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.veyndan.paper.reddit.image.Decoder;

import java.io.InputStream;

public class CustomDecoder implements Decoder {

    @Override
    public Bitmap decodeInputStream(final InputStream inputStream) {
        return BitmapFactory.decodeStream(inputStream);
    }
}
