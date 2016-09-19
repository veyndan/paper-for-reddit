package com.veyndan.redditclient;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeTraverser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TYPE_CONTENT, TYPE_PROGRESS, TYPE_MORE})
        public @interface Type {}

        public static final int TYPE_CONTENT = 0;
        public static final int TYPE_PROGRESS = 1;
        public static final int TYPE_MORE = 2;

        private final T data;
        @Type private final int type;
        @IntRange(from = 0) private int depth;

        public Node(final T data, @Type final int type) {
            this(data, type, 0);
        }

        public Node(final T data, @Type final int type, @IntRange(from = 0) final int depth) {
            this.data = data;
            this.type = type;
            this.depth = depth;
        }

        public T getData() {
            return data;
        }

        @Type
        public int getType() {
            return type;
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
                ", \"data\": \"" + data + '"' +
                ", \"children\":" + children +
                '}';
    }
}
