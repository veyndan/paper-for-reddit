package com.veyndan.paper.reddit;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface Filter {

    @NonNull
    Bundle requestFilter();
}
