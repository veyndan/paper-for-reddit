package com.veyndan.paper.reddit.api.reddit.model;

public interface Votable {

    int ups();

    int downs();

    /**
     * How the logged-in user has voted on the submission. {@code true} = upvoted,
     * {@code false} = downvoted, {@code null} = no vote.
     */
    Boolean likes();
}
