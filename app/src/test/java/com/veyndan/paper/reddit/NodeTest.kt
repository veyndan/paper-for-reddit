package com.veyndan.paper.reddit

import com.veyndan.paper.reddit.util.Node
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class NodeTest {

    @Test
    fun descendantCount_unknownDescendantCount_isCorrect() {
        val node30 = setAsChildren<Void>()

        val node20 = setAsChildren(node30)
        val node21 = setAsChildren<Void>()
        val node22 = setAsChildren<Void>()

        val node10 = setAsChildren<Void>()
        val node11 = setAsChildren(node20, node21)
        val node12 = setAsChildren(node22)

        val tree = setAsChildren(node10, node11, node12)

        tree.preOrderTraverse(0)
                .flatMapSingle<Int> { it.descendantCount() }
                .test()
                .assertValues(7, 0, 3, 1, 0, 0, 1, 0)
                .assertComplete()
    }

    @Test
    fun descendantCount_knownDescendantCount_isCorrect() {
        val node30 = setAsChildren<Void>(0)

        val node20 = setAsChildren(1, node30)
        val node21 = setAsChildren<Void>(0)
        val node22 = setAsChildren<Void>(0)

        val node10 = setAsChildren<Void>(0)
        val node11 = setAsChildren(3, node20, node21)
        val node12 = setAsChildren(1, node22)

        val tree = setAsChildren(7, node10, node11, node12)

        tree.preOrderTraverse(0)
                .flatMapSingle<Int> { it.descendantCount() }
                .test()
                .assertValues(7, 0, 3, 1, 0, 0, 1, 0)
                .assertComplete()
    }

    @Test
    fun depth_isCorrect() {
        val node30 = setAsChildren<Void>()

        val node20 = setAsChildren(node30)
        val node21 = setAsChildren<Void>()
        val node22 = setAsChildren<Void>()

        val node10 = setAsChildren<Void>()
        val node11 = setAsChildren(node20, node21)
        val node12 = setAsChildren(node22)

        val tree = setAsChildren(node10, node11, node12)

        tree.preOrderTraverse(0)
                .map { it.depth }
                .test()
                .assertValues(0, 1, 1, 2, 3, 2, 1, 2)
                .assertComplete()
    }

    private fun <T> setAsChildren(descendantCount: Int, vararg children: Node<T>): Node<T> {
        return object : Node<T>() {

            override fun children(): Observable<Node<T>> {
                return Observable.fromArray(*children)
            }

            override fun descendantCount(): Single<Int> {
                return Single.just(descendantCount)
            }
        }
    }

    private fun <T> setAsChildren(vararg children: Node<T>): Node<T> {
        return object : Node<T>() {

            override fun children(): Observable<Node<T>> {
                return Observable.fromArray(*children)
            }
        }
    }
}
