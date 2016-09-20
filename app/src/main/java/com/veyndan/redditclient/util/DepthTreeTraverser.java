package com.veyndan.redditclient.util;

import android.support.annotation.NonNull;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A tree traverser which also sets the depth of the nodes. Most functionality copied from
 * Google Guava's tree traverser class.
 *
 * @see com.google.common.collect.TreeTraverser
 */
public abstract class DepthTreeTraverser<T> {

    public abstract Iterable<T> children(T root);

    public final FluentIterable<Node<T>> preOrderTraversal(final T root) {
        checkNotNull(root);
        return new FluentIterable<Node<T>>() {
            @Override
            public UnmodifiableIterator<Node<T>> iterator() {
                return preOrderIterator(root);
            }
        };
    }

    private UnmodifiableIterator<Node<T>> preOrderIterator(final T root) {
        return new PreOrderIterator(root);
    }

    private final class PreOrderIterator extends UnmodifiableIterator<Node<T>> {

        private final Deque<Node<Iterator<T>>> stack;

        PreOrderIterator(@NonNull final T root) {
            this.stack = new ArrayDeque<>();
            stack.addLast(new Node<>(Iterators.singletonIterator(checkNotNull(root)), 0));
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Node<T> next() {
            final Node<Iterator<T>> itrNode = stack.getLast(); // throws NSEE if empty
            final Iterator<T> itr = itrNode.getData();
            final int depth = itrNode.getDepth();
            final T result = checkNotNull(itr.next());
            final Node<T> node = new Node<>(result, depth);

            if (!itr.hasNext()) {
                stack.removeLast();
            }
            final Iterator<T> childItr = children(result).iterator();
            if (childItr.hasNext()) {
                stack.addLast(new Node<>(childItr, depth + 1));
            }

            return node;
        }
    }
}
