package com.veyndan.paper.reddit;

import android.os.Bundle;

public interface Filter {

    String COMMENTS_SUBREDDIT = "comments_subreddit";
    String COMMENTS_ARTICLE = "comments_article";

    String TIME_PERIOD_POSITION = "time_period_value";

    String SUBREDDIT_NAME = "subreddit_name";

    String USER_NAME = "user_name";
    String USER_COMMENTS = "user_comments";
    String USER_SUBMITTED = "user_submitted";
    String USER_GILDED = "user_gilded";

    Bundle requestFilter();
}
