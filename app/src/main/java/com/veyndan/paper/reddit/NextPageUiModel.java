package com.veyndan.paper.reddit;

import com.veyndan.paper.reddit.util.Node;

import java.util.Collections;
import java.util.List;

public final class NextPageUiModel<T> {

    public static <T> NextPageUiModel<T> tree(final Node<T> tree) {
        return new NextPageUiModel<>(Collections.singletonList(tree));
    }

    public static <T> NextPageUiModel<T> forest(final List<Node<T>> forest) {
        return new NextPageUiModel<>(forest);
    }

    private final List<Node<T>> forest;

    private NextPageUiModel(final List<Node<T>> forest) {
        this.forest = forest;
    }

    public List<Node<T>> getForest() {
        return forest;
    }
}
