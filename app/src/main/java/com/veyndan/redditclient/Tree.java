package com.veyndan.redditclient;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeTraverser;

import java.util.List;

public class Tree<T> {

    private final TreeTraverser<Tree<T>> treeTraverser = new TreeTraverser<Tree<T>>() {
        @Override
        public Iterable<Tree<T>> children(@NonNull final Tree<T> root) {
            return root.children;
        }
    };

    private final Node<T> node;
    private final List<Tree<T>> children;

    public Tree(final Node<T> node, final List<Tree<T>> children) {
        this.node = node;
        this.children = children;
    }

    public Node<T> getNode() {
        return node;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public ImmutableList<Node<T>> toFlattenedNodeList() {
        return treeTraverser.preOrderTraversal(this).transform(input -> input.node).toList();
    }

    public void generateDepths() {
        generateDepths(this, 0);
    }

    private void generateDepths(final Tree<T> tree, final int depth) {
        tree.node.depth = depth;
        for (final Tree<T> child : tree.children) {
            generateDepths(child, depth + 1);
        }
    }

    public static class Node<T> {

        @NonNull private final T data;
        @IntRange(from = 0) private int depth;

        public Node(@NonNull final T data) {
            this(data, 0);
        }

        public Node(@NonNull final T data, @IntRange(from = 0) final int depth) {
            this.data = data;
            this.depth = depth;
        }

        @NonNull
        public T getData() {
            return data;
        }

        @IntRange(from = 0)
        public int getDepth() {
            return depth;
        }
    }

    @Override
    public String toString() {
        // Change print type to json so can use online tools for well formatted printing.
        return "{\"type\": \"Tree\"" +
                ", \"data\": \"" + node + '"' +
                ", \"children\":" + children +
                '}';
    }
}
