package com.veyndan.paper.reddit.util

import android.content.Intent
import android.os.Bundle

object IntentUtils {

    /**
     * Returns the extras of the supplied Intent. If the extras are null, a new Bundle is returned.
     */
    fun getExtras(intent: Intent): Bundle {
        // TODO Replace with immutable Bundle which is a constant in this class so the same value
        // is returned on each call. Can't just inherit from Bundle as Bundle is final.
        return intent.extras ?: Bundle()
    }
}
