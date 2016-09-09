package com.veyndan.redditclient;

import android.support.annotation.NonNull;

import com.google.common.collect.TreeTraverser;

import java.util.List;

public class Tree<T> {

    private final TreeTraverser<Tree<T>> treeTraverser = new TreeTraverser<Tree<T>>() {
        @Override
        public Iterable<Tree<T>> children(@NonNull final Tree<T> root) {
            return root.children;
        }
    };

    private final T data;
    private final List<Tree<T>> children;

    private int depth;

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

    public List<T> toFlattenedDataList() {
        return treeTraverser.preOrderTraversal(this).transform(input -> input.data).toList();
    }

    public List<Integer> toFlattenedDepthList() {
        return treeTraverser.preOrderTraversal(this).transform(input -> input.depth).toList();
    }

    void generateDepths() {
        generateDepths(this, 0);
    }

    private void generateDepths(final Tree<T> tree, final int depth) {
        tree.depth = depth;
        for (final Tree<T> child : tree.children) {
            generateDepths(child, depth + 1);
        }
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
