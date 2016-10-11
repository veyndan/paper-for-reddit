package com.veyndan.redditclient;

import android.os.Bundle;

public interface Filter {

    String SUBREDDIT_NAME = "subreddit_name";

    String USER_NAME = "user_name";
    String USER_COMMENTS = "user_comments";
    String USER_SUBMITTED = "user_submitted";
    String USER_GILDED = "user_gilded";

    Bundle requestFilter();
}
