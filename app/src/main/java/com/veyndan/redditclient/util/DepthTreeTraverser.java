package com.veyndan.redditclient.util;

import android.support.annotation.NonNull;
import android.util.Pair;

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

    public final FluentIterable<Pair<T, Integer>> preOrderTraversal(final T root) {
        checkNotNull(root);
        return new FluentIterable<Pair<T, Integer>>() {
            @Override
            public UnmodifiableIterator<Pair<T, Integer>> iterator() {
                return preOrderIterator(root);
            }
        };
    }

    private UnmodifiableIterator<Pair<T, Integer>> preOrderIterator(final T root) {
        return new PreOrderIterator(root);
    }

    private final class PreOrderIterator extends UnmodifiableIterator<Pair<T, Integer>> {

        private final Deque<Pair<Iterator<T>, Integer>> stack;

        PreOrderIterator(@NonNull final T root) {
            this.stack = new ArrayDeque<>();
            stack.addLast(new Pair<>(Iterators.singletonIterator(checkNotNull(root)), 0));
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Pair<T, Integer> next() {
            final Pair<Iterator<T>, Integer> itrPair = stack.getLast(); // throws NSEE if empty

            final Iterator<T> itr = itrPair.first;
            final int depth = itrPair.second;

            final T result = checkNotNull(itr.next());
            final Pair<T, Integer> pair = new Pair<>(result, depth);

            if (!itr.hasNext()) {
                stack.removeLast();
            }
            final Iterator<T> childItr = children(result).iterator();
            if (childItr.hasNext()) {
                stack.addLast(new Pair<>(childItr, depth + 1));
            }

            return pair;
        }
    }
}
