package com.veyndan.paper.reddit.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

public final class IntentUtils {

    /**
     * Returns the extras of the supplied Intent. If the extras are null, a new Bundle is returned.
     */
    @NonNull
    public static Bundle getExtras(@NonNull final Intent intent) {
        // TODO Replace with immutable Bundle which is a constant in this class so the same value
        // is returned on each call. Can't just inherit from Bundle as Bundle is final.
        final Bundle extras = intent.getExtras();
        return extras == null ? new Bundle() : extras;
    }
}
