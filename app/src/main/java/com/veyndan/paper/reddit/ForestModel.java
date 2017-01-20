package com.veyndan.paper.reddit;

import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import com.veyndan.paper.reddit.util.Node;
import com.veyndan.paper.reddit.util.list.command.ListCommand;

import java.util.ArrayList;
import java.util.List;

public final class ForestModel<T> implements Model {

    private final List<Node<T>> forest;
    private final List<AdapterCommand> adapterCommands;
    private final List<ListCommand<Node<T>>> listCommands;

    private ForestModel(final Builder<T> builder) {
        forest = builder.forest;
        adapterCommands = builder.adapterCommands;
        listCommands = builder.listCommands;
    }

    public List<Node<T>> getPreviousForest() {
        return forest;
    }

    public List<AdapterCommand> getAdapterCommands() {
        return adapterCommands;
    }

    public List<ListCommand<Node<T>>> getListCommands() {
        return listCommands;
    }

    public static class Builder<T> {

        private final List<Node<T>> forest = new ArrayList<>();
        private final List<AdapterCommand> adapterCommands = new ArrayList<>();
        private final List<ListCommand<Node<T>>> listCommands = new ArrayList<>();

        public Builder<T> previousTree(final Node<T> tree) {
            forest.add(tree);
            return this;
        }

        public Builder<T> previousForest(final List<? extends Node<T>> forest) {
            this.forest.addAll(forest);
            return this;
        }

        public Builder<T> adapterCommand(final AdapterCommand adapterCommand) {
            adapterCommands.add(adapterCommand);
            return this;
        }

        public Builder<T> adapterCommands(final List<AdapterCommand> adapterCommands) {
            this.adapterCommands.addAll(adapterCommands);
            return this;
        }

        public Builder<T> listCommand(final ListCommand<Node<T>> listCommand) {
            listCommands.add(listCommand);
            return this;
        }

        public Builder<T> listCommands(final List<ListCommand<Node<T>>> listCommands) {
            this.listCommands.addAll(listCommands);
            return this;
        }

        public ForestModel<T> build() {
            return new ForestModel<>(this);
        }
    }
}
