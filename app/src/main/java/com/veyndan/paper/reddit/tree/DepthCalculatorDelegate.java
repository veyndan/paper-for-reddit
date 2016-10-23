package com.veyndan.paper.reddit.tree;

import android.support.annotation.IntRange;

public interface DepthCalculatorDelegate {

    @IntRange(from = 0)
    int depthForPosition(@IntRange(from = 0) int position);
}
