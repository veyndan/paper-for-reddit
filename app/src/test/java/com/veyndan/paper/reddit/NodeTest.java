package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.veyndan.paper.reddit.util.Node;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class NodeTest {

    @Test
    public void descendantCount_unknownDescendantCount_isCorrect() {
        final Node<Void> node30 = setAsChildren();

        final Node<Void> node20 = setAsChildren(node30);
        final Node<Void> node21 = setAsChildren();
        final Node<Void> node22 = setAsChildren();

        final Node<Void> node10 = setAsChildren();
        final Node<Void> node11 = setAsChildren(node20, node21);
        final Node<Void> node12 = setAsChildren(node22);

        final Node<Void> tree = setAsChildren(node10, node11, node12);

        tree.preOrderTraverse(0)
                .flatMapSingle(Node::descendantCount)
                .test()
                .assertValues(7, 0, 3, 1, 0, 0, 1, 0)
                .assertComplete();
    }

    @Test
    public void descendantCount_knownDescendantCount_isCorrect() {
        final Node<Void> node30 = setAsChildren(0);

        final Node<Void> node20 = setAsChildren(1, node30);
        final Node<Void> node21 = setAsChildren(0);
        final Node<Void> node22 = setAsChildren(0);

        final Node<Void> node10 = setAsChildren(0);
        final Node<Void> node11 = setAsChildren(3, node20, node21);
        final Node<Void> node12 = setAsChildren(1, node22);

        final Node<Void> tree = setAsChildren(7, node10, node11, node12);

        tree.preOrderTraverse(0)
                .flatMapSingle(Node::descendantCount)
                .test()
                .assertValues(7, 0, 3, 1, 0, 0, 1, 0)
                .assertComplete();
    }

    @Test
    public void depth_isCorrect() {
        final Node<Void> node30 = setAsChildren();

        final Node<Void> node20 = setAsChildren(node30);
        final Node<Void> node21 = setAsChildren();
        final Node<Void> node22 = setAsChildren();

        final Node<Void> node10 = setAsChildren();
        final Node<Void> node11 = setAsChildren(node20, node21);
        final Node<Void> node12 = setAsChildren(node22);

        final Node<Void> tree = setAsChildren(node10, node11, node12);

        tree.preOrderTraverse(0)
                .map(Node::depth)
                .test()
                .assertValues(0, 1, 1, 2, 3, 2, 1, 2)
                .assertComplete();
    }

    private static <T> Node<T> setAsChildren(final int descendantCount, final Node<T>... children) {
        return new Node<T>() {

            @Nullable
            @Override
            public Integer degree() {
                return null;
            }

            @NonNull
            @Override
            public Observable<Node<T>> children() {
                return Observable.fromArray(children);
            }

            @NonNull
            @Override
            public Single<Integer> descendantCount() {
                return Single.just(descendantCount);
            }
        };
    }

    private static <T> Node<T> setAsChildren(final Node<T>... children) {
        return new Node<T>() {

            @Nullable
            @Override
            public Integer degree() {
                return null;
            }

            @NonNull
            @Override
            public Observable<Node<T>> children() {
                return Observable.fromArray(children);
            }
        };
    }
}
