package com.veyndan.redditclient;

import java.util.List;

public class Tree<T> {

    private final T data;
    private final List<Tree<T>> children;

    public Tree(final T data, final List<Tree<T>> children) {
        this.data = data;
        this.children = children;
    }

    public T getData() {
        return data;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        // Change print type to json so can use online tools for well formatted printing.
        return "{\"type\": \"Tree\"" +
                ", \"data\": \"" + data + '"' +
                ", \"children\":" + children +
                '}';
    }
}
