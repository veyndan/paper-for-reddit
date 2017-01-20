package com.veyndan.paper.reddit.util.list.command;

import java.util.Collection;
import java.util.List;

public class AddAllCommand<E> implements ListCommand<E> {

    private final Collection<? extends E> elements;

    public AddAllCommand(final Collection<? extends E> elements) {
        this.elements = elements;
    }

    @Override
    public void execute(final List<E> list) {
        list.addAll(elements);
    }

    public Collection<? extends E> addAllElements() {
        return elements;
    }
}
