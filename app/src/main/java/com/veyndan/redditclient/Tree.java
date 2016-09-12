package com.veyndan.redditclient;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeTraverser;

import java.util.List;

import static android.R.attr.data;

public class Tree<T> {

    private final TreeTraverser<Tree<T>> treeTraverser = new TreeTraverser<Tree<T>>() {
        @Override
        public Iterable<Tree<T>> children(@NonNull final Tree<T> root) {
            return root.children;
        }
    };

    private final Node<T> node;
    private final List<Tree<T>> children;

    private int depth;

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

    public static class Node<T> {

        private final T data;
        private final boolean stub;

        public Node(final T data, final boolean stub) {
            this.data = data;
            this.stub = stub;
        }

        public T getData() {
            return data;
        }

        public boolean isStub() {
            return stub;
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
