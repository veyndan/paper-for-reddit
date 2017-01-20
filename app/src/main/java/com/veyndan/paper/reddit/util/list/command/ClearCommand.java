package com.veyndan.paper.reddit.util.list.command;

import java.util.List;

public class ClearCommand<E> implements ListCommand<E> {

    @Override
    public void execute(final List<E> list) {
        list.clear();
    }
}
