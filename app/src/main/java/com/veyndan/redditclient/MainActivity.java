package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import butterknife.ButterKnife;
import rawjava.network.QueryBuilder;
import rawjava.network.Sort;
import rawjava.network.TimePeriod;

public class MainActivity extends BaseActivity {

    private static final Map<Integer, Pair<Sort, Optional<TimePeriod>>> filters = ImmutableMap.<Integer, Pair<Sort, Optional<TimePeriod>>>builder()
            .put(R.id.action_sort_hot, Pair.create(Sort.HOT, Optional.absent()))
            .put(R.id.action_sort_new, Pair.create(Sort.NEW, Optional.absent()))
            .put(R.id.action_sort_rising, Pair.create(Sort.RISING, Optional.absent()))
            .put(R.id.action_sort_controversial_hour, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.HOUR)))
            .put(R.id.action_sort_controversial_day, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.DAY)))
            .put(R.id.action_sort_controversial_week, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.WEEK)))
            .put(R.id.action_sort_controversial_month, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.MONTH)))
            .put(R.id.action_sort_controversial_year, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.YEAR)))
            .put(R.id.action_sort_controversial_all, Pair.create(Sort.CONTROVERSIAL, Optional.of(TimePeriod.ALL)))
            .put(R.id.action_sort_top_hour, Pair.create(Sort.TOP, Optional.of(TimePeriod.HOUR)))
            .put(R.id.action_sort_top_day, Pair.create(Sort.TOP, Optional.of(TimePeriod.DAY)))
            .put(R.id.action_sort_top_week, Pair.create(Sort.TOP, Optional.of(TimePeriod.WEEK)))
            .put(R.id.action_sort_top_month, Pair.create(Sort.TOP, Optional.of(TimePeriod.MONTH)))
            .put(R.id.action_sort_top_year, Pair.create(Sort.TOP, Optional.of(TimePeriod.YEAR)))
            .put(R.id.action_sort_top_all, Pair.create(Sort.TOP, Optional.of(TimePeriod.ALL)))
            .build();

    private PostsFragment postsFragment;

    private String subreddit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        subreddit = extras.getString("subreddit", "all");

        filterPosts(filters.get(R.id.action_sort_hot));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Pair<Sort, Optional<TimePeriod>> filter = filters.get(item.getItemId());

        // MenuItem id found in the list of handled filters
        if (filter != null) {
            filterPosts(filter);
            return true;
        }

        return false;
    }

    private void filterPosts(final Pair<Sort, Optional<TimePeriod>> filter) {
        final Sort sort = filter.first;
        final Optional<TimePeriod> timePeriod = filter.second;

        final QueryBuilder query = new QueryBuilder();

        if (timePeriod.isPresent()) {
            query.t(timePeriod.get());
        }

        postsFragment.setFilter(new SubredditFilter(subreddit, sort, query));
    }
}
