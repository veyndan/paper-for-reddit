package com.veyndan.paper.reddit.util;

import io.reactivex.Maybe;

public class Maybes {

    private Maybes() {
        throw new AssertionError("No instances.");
    }

    public static <T> Maybe<T> ofNullable(final T value) {
        return value == null ? Maybe.empty() : Maybe.just(value);
    }
}
