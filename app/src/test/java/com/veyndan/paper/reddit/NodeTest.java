package com.veyndan.paper.reddit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.veyndan.paper.reddit.util.Node;

import org.junit.Test;

import io.reactivex.Observable;

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

        tree.preOrderTraverse()
                .map(Node::descendantCount)
                .test()
                .assertValues(7, 0, 3, 1, 0, 0, 1, 0)
                .assertComplete();
    }

    @Test
    public void descendantCount_knownDescendantCount_isCorrect() {
        final Node<Void> node30 = setAsChildren();
        node30.descendantCount(0);

        final Node<Void> node20 = setAsChildren(node30);
        node20.descendantCount(1);

        final Node<Void> node21 = setAsChildren();
        node21.descendantCount(0);

        final Node<Void> node22 = setAsChildren();
        node22.descendantCount(0);

        final Node<Void> node10 = setAsChildren();
        node10.descendantCount(0);

        final Node<Void> node11 = setAsChildren(node20, node21);
        node11.descendantCount(3);

        final Node<Void> node12 = setAsChildren(node22);
        node12.descendantCount(1);

        final Node<Void> tree = setAsChildren(node10, node11, node12);
        tree.descendantCount(7);

        tree.preOrderTraverse()
                .map(Node::descendantCount)
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

        tree.preOrderTraverse()
                .map(Node::depth)
                .test()
                .assertValues(0, 1, 1, 2, 3, 2, 1, 2)
                .assertComplete();
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
