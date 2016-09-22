package com.veyndan.redditclient.util;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

public abstract class Node {

    @IntRange(from = 0) private int depth;

    public Node() {
        this(0);
    }

    public Node(@IntRange(from = 0) final int depth) {
        this.depth = depth;
    }

    @IntRange(from = 0)
    public int getDepth() {
        return depth;
    }

    public void setDepth(@IntRange(from = 0) final int depth) {
        this.depth = depth;
    }

    @NonNull
    public abstract List<Node> getChildren();
}
