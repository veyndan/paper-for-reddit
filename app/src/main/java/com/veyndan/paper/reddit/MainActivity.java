package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.api.reddit.network.TimePeriod;
import com.veyndan.paper.reddit.api.reddit.network.User;
import com.veyndan.paper.reddit.databinding.ActivityMainBinding;
import com.veyndan.paper.reddit.post.PostsFragment;
import com.veyndan.paper.reddit.util.IntentUtils;

import io.reactivex.Single;
import retrofit2.Response;

@DeepLink({
        "http://reddit.com/u/{" + Filter.USER_NAME + '}',
        "http://reddit.com/user/{" + Filter.USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    private static final Reddit REDDIT = new Reddit(Config.REDDIT_CREDENTIALS);

    private PostsFragment postsFragment;

    private String subreddit;

    @Override
    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

        final Intent intent = getIntent();

        final Single<Response<Thing<Listing>>> defaultRequest = REDDIT.subreddit("all", Sort.HOT, null);
        final Single<Response<Thing<Listing>>> mergedFilters = mergeFilters(IntentUtils.getExtras(intent), defaultRequest);
        postsFragment.setRequest(mergedFilters);
    }

    @NonNull
    private Single<Response<Thing<Listing>>> mergeFilters(@NonNull final Bundle bundle,
                                                          @NonNull final Single<Response<Thing<Listing>>> defaultRequest) {
        if (bundle.containsKey(Filter.COMMENTS_SUBREDDIT)) {
            final String subreddit = bundle.getString(Filter.COMMENTS_SUBREDDIT);
            final String article = bundle.getString(Filter.COMMENTS_ARTICLE);
            return REDDIT.subredditComments(subreddit, article);
        }

        final TimePeriod[] timePeriods = {
                TimePeriod.HOUR,
                TimePeriod.DAY,
                TimePeriod.WEEK,
                TimePeriod.MONTH,
                TimePeriod.YEAR,
                TimePeriod.ALL
        };

        final TimePeriod timePeriod = timePeriods[bundle.getInt(Filter.TIME_PERIOD_POSITION)];

        subreddit = bundle.getString(Filter.SUBREDDIT_NAME);

        final String username = bundle.getString(Filter.USER_NAME);
        final boolean comments = bundle.getBoolean(Filter.USER_COMMENTS);
        final boolean submitted = bundle.getBoolean(Filter.USER_SUBMITTED);
        final boolean gilded = bundle.getBoolean(Filter.USER_GILDED);

        if (TextUtils.isEmpty(subreddit) && TextUtils.isEmpty(username)) {
            return defaultRequest;
        } else if (!TextUtils.isEmpty(subreddit) && TextUtils.isEmpty(username)) {
            return REDDIT.subreddit(subreddit, Sort.HOT, null);
        } else if (TextUtils.isEmpty(subreddit)) { // && !TextUtils.isEmpty(username)
            final User user;
            if (bundle.getBoolean(DeepLink.IS_DEEP_LINK, false)) {
                user = User.OVERVIEW;
            } else if ((comments == submitted) && gilded) {
                user = User.GILDED;
            } else if ((comments != submitted) && gilded) {
                throw new UnsupportedOperationException("User state unsure");
            } else if (comments && submitted) {
                user = User.OVERVIEW;
            } else if (comments) {
                user = User.COMMENTS;
            } else if (submitted) {
                user = User.SUBMITTED;
            } else {
                throw new UnsupportedOperationException("User state unsure");
            }

            return REDDIT.user(username, user, timePeriod);
        } else { // !TextUtils.isEmpty(subreddit) && !TextUtils.isEmpty(username)
            // TODO Concatenate the subreddit and username search query
            return defaultRequest;
        }
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
            case R.id.action_filter:
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FilterFragment filterFragment = FilterFragment.newInstance();
                filterFragment.show(fragmentManager, "fragment_filter");
                return true;
            case R.id.action_sort_hot:
                postsFragment.setRequest(REDDIT.subreddit(subreddit, Sort.HOT, null));
                return true;
            case R.id.action_sort_new:
                postsFragment.setRequest(REDDIT.subreddit(subreddit, Sort.NEW, null));
                return true;
            case R.id.action_sort_rising:
                postsFragment.setRequest(REDDIT.subreddit(subreddit, Sort.RISING, null));
                return true;
            case R.id.action_sort_controversial:
                postsFragment.setRequest(REDDIT.subreddit(subreddit, Sort.CONTROVERSIAL, null));
                return true;
            case R.id.action_sort_top:
                postsFragment.setRequest(REDDIT.subreddit(subreddit, Sort.TOP, null));
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
