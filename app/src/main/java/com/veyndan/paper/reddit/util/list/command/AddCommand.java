package com.veyndan.paper.reddit.util.list.command;

import java.util.List;

public class AddCommand<E> implements ListCommand<E> {

    private final E element;

    public AddCommand(final E element) {
        this.element = element;
    }

    @Override
    public void execute(final List<E> list) {
        list.add(element);
    }

    public E addElement() {
        return element;
    }
}
