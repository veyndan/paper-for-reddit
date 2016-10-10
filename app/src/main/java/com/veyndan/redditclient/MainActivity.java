package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.api.reddit.network.TimePeriod;
import com.veyndan.redditclient.api.reddit.network.User;
import com.veyndan.redditclient.post.PostsFragment;
import com.veyndan.redditclient.post.model.Post;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@DeepLink({
        "http://reddit.com/u/{username}",
        "http://reddit.com/user/{username}"
})
public class MainActivity extends BaseActivity {

    private PostsFragment postsFragment;

    private String subreddit;

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

        if (extras.containsKey("username")) {
            final String username = extras.getString("username");

            final ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(username);

            postsFragment.setFilter(new UserFilter(username, User.OVERVIEW));
        } else {
            subreddit = extras.getString("subreddit", "all");
            postsFragment.setFilter(new SubredditFilter(subreddit, Sort.HOT));
        }

        final PostsFragment commentsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_comments);

        EventBus.INSTANCE.toObserverable()
                .subscribeOn(Schedulers.io())
                .ofType(Post.class)
                .filter(post -> commentsFragment != null && commentsFragment.isVisible())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(post -> {
                    final String fullname = post.getFullname();
                    final String article = fullname.substring(3, fullname.length());

                    commentsFragment.setFilter(new CommentsFilter(post.getSubreddit(), article));
                }, Timber::e);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account_add:
                final Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_sort_hot:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.HOT));
                return true;
            case R.id.action_sort_new:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.NEW));
                return true;
            case R.id.action_sort_rising:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.RISING));
                return true;
            case R.id.action_sort_controversial_hour:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.HOUR)));
                return true;
            case R.id.action_sort_controversial_day:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.DAY)));
                return true;
            case R.id.action_sort_controversial_week:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.WEEK)));
                return true;
            case R.id.action_sort_controversial_month:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.MONTH)));
                return true;
            case R.id.action_sort_controversial_year:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.YEAR)));
                return true;
            case R.id.action_sort_controversial_all:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.ALL)));
                return true;
            case R.id.action_sort_top_hour:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.HOUR)));
                return true;
            case R.id.action_sort_top_day:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.DAY)));
                return true;
            case R.id.action_sort_top_week:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.WEEK)));
                return true;
            case R.id.action_sort_top_month:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.MONTH)));
                return true;
            case R.id.action_sort_top_year:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.YEAR)));
                return true;
            case R.id.action_sort_top_all:
                postsFragment.setFilter(new SubredditFilter(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.ALL)));
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String code = data.getStringExtra("code");
        }
    }
}
