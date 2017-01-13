package com.veyndan.paper.reddit;

import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import com.veyndan.paper.reddit.util.Node;

import java.util.ArrayList;
import java.util.List;

public final class ForestModel<T> implements Model {

    private final List<Node<T>> trees;
    private final List<AdapterCommand> adapterCommands;

    private ForestModel(final Builder<T> builder) {
        trees = builder.trees;
        adapterCommands = builder.adapterCommands;
    }

    public List<Node<T>> getTrees() {
        return trees;
    }

    public static class Builder<T> {

        private final List<Node<T>> trees = new ArrayList<>();
        private final List<AdapterCommand> adapterCommands = new ArrayList<>();

        public Builder<T> tree(final Node<T> tree) {
            trees.add(tree);
            return this;
        }

        public Builder<T> trees(final List<Node<T>> trees) {
            this.trees.addAll(trees);
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

        public ForestModel<T> build() {
            return new ForestModel<>(this);
        }
    }
}
