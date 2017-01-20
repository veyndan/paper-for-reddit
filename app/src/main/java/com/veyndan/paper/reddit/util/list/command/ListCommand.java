package com.veyndan.paper.reddit.util.list.command;

import java.util.List;

public interface ListCommand<E> {

    void execute(List<E> list);
}
