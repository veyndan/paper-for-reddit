package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.collect.ImmutableMap;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.api.reddit.network.TimePeriod;
import com.veyndan.redditclient.post.PostsFragment;

import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private Map<Integer, SubredditFilter> filters;

    private PostsFragment postsFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        // Instantiate new Bundle if null so extras.get* will always be the default values.
        if (extras == null) {
            extras = new Bundle();
        }

        final String subreddit = extras.getString("subreddit", "all");

        filters = ImmutableMap.<Integer, SubredditFilter>builder()
                .put(R.id.action_sort_hot, new SubredditFilter(subreddit, Sort.HOT))
                .put(R.id.action_sort_new, new SubredditFilter(subreddit, Sort.NEW))
                .put(R.id.action_sort_rising, new SubredditFilter(subreddit, Sort.RISING))
                .put(R.id.action_sort_controversial_hour, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.HOUR)))
                .put(R.id.action_sort_controversial_day, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.DAY)))
                .put(R.id.action_sort_controversial_week, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.WEEK)))
                .put(R.id.action_sort_controversial_month, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.MONTH)))
                .put(R.id.action_sort_controversial_year, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.YEAR)))
                .put(R.id.action_sort_controversial_all, new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.ALL)))
                .put(R.id.action_sort_top_hour, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.HOUR)))
                .put(R.id.action_sort_top_day, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.DAY)))
                .put(R.id.action_sort_top_week, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.WEEK)))
                .put(R.id.action_sort_top_month, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.MONTH)))
                .put(R.id.action_sort_top_year, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.YEAR)))
                .put(R.id.action_sort_top_all, new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.ALL)))
                .build();

        postsFragment.setFilter(new SubredditFilter(subreddit, Sort.HOT));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_account_add) {
            final Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        final SubredditFilter filter = filters.get(item.getItemId());

        // MenuItem id found in the list of handled filters
        if (filter != null) {
            postsFragment.setFilter(filter);
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String code = data.getStringExtra("code");
        }
    }
}
