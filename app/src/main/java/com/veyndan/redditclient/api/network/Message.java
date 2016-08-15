package com.veyndan.redditclient.api.network;

public enum Message {
    INBOX, UNREAD, SENT;

    @Override
    public String toString() {
        // Nothing to do with JSON deserialization, but is
        // the required format for URL query parameters.
        return name().toLowerCase();
    }
}
