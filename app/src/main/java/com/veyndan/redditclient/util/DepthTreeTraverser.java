package com.veyndan.redditclient.util;

import android.support.annotation.NonNull;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.veyndan.redditclient.Tree;

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
public abstract class DepthTreeTraverser<T extends Tree> {

    public abstract Iterable<T> children(T root);

    public final FluentIterable<T> preOrderTraversal(final T root) {
        checkNotNull(root);
        return new FluentIterable<T>() {
            @Override
            public UnmodifiableIterator<T> iterator() {
                return preOrderIterator(root);
            }
        };
    }

    private UnmodifiableIterator<T> preOrderIterator(final T root) {
        return new PreOrderIterator(root);
    }

    private final class PreOrderIterator extends UnmodifiableIterator<T> {

        private final Deque<Tree.Node<Iterator<T>>> stack;

        PreOrderIterator(@NonNull final T root) {
            this.stack = new ArrayDeque<>();
            stack.addLast(new Tree.Node<>(Iterators.singletonIterator(checkNotNull(root)), 0));
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            final Tree.Node<Iterator<T>> itrNode = stack.getLast(); // throws NSEE if empty
            final Iterator<T> itr = itrNode.getData();
            final int depth = itrNode.getDepth();
            final T result = checkNotNull(itr.next());

            result.getNode().setDepth(depth);

            if (!itr.hasNext()) {
                stack.removeLast();
            }
            final Iterator<T> childItr = children(result).iterator();
            if (childItr.hasNext()) {
                stack.addLast(new Tree.Node<>(childItr, depth + 1));
            }

            return result;
        }
    }
}
