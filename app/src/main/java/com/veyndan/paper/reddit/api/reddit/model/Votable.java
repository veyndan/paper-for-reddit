package com.veyndan.paper.reddit.api.reddit.model;

import com.veyndan.paper.reddit.api.reddit.network.VoteDirection;

public interface Votable {

    int getUps();

    int getDowns();

    VoteDirection getLikes();
}
