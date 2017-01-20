package com.veyndan.paper.reddit.util.list.command;

import java.util.List;

public class RemoveCommand<E> implements ListCommand<E> {

    private final int index;

    public RemoveCommand(final int index) {
        this.index = index;
    }

    @Override
    public void execute(final List<E> list) {
        list.remove(index);
    }

    public int removeIndex() {
        return index;
    }
}
