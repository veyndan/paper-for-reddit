package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.databinding.ActivityMainBinding;
import com.veyndan.paper.reddit.post.PostsFragment;
import com.veyndan.paper.reddit.util.IntentUtils;

import io.reactivex.Single;
import retrofit2.Response;

@DeepLink({
        "http://reddit.com/u/{" + Reddit.Filter.USER_NAME + '}',
        "http://reddit.com/user/{" + Reddit.Filter.USER_NAME + '}'
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
        final Bundle intentExtras = IntentUtils.getExtras(intent);

        if (intentExtras.isEmpty()) {
            intentExtras.putInt(Reddit.Filter.NODE_DEPTH, 0);
            intentExtras.putString(Reddit.Filter.SUBREDDIT_NAME, "all");
        }

        subreddit = intentExtras.getString(Reddit.Filter.SUBREDDIT_NAME);

        final Single<Response<Thing<Listing>>> mergedFilters = REDDIT.query(intentExtras, Sort.HOT);
        postsFragment.setRequest(mergedFilters);
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
                final Bundle redditQueryParamsHot = new Bundle();
                redditQueryParamsHot.putInt(Reddit.Filter.NODE_DEPTH, 0);
                redditQueryParamsHot.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

                postsFragment.setRequest(REDDIT.query(redditQueryParamsHot, Sort.HOT));
                return true;
            case R.id.action_sort_new:
                final Bundle redditQueryParamsNew = new Bundle();
                redditQueryParamsNew.putInt(Reddit.Filter.NODE_DEPTH, 0);
                redditQueryParamsNew.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

                postsFragment.setRequest(REDDIT.query(redditQueryParamsNew, Sort.NEW));
                return true;
            case R.id.action_sort_rising:
                final Bundle redditQueryParamsRising = new Bundle();
                redditQueryParamsRising.putInt(Reddit.Filter.NODE_DEPTH, 0);
                redditQueryParamsRising.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

                postsFragment.setRequest(REDDIT.query(redditQueryParamsRising, Sort.RISING));
                return true;
            case R.id.action_sort_controversial:
                final Bundle redditQueryParamsControversial = new Bundle();
                redditQueryParamsControversial.putInt(Reddit.Filter.NODE_DEPTH, 0);
                redditQueryParamsControversial.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

                postsFragment.setRequest(REDDIT.query(redditQueryParamsControversial, Sort.CONTROVERSIAL));
                return true;
            case R.id.action_sort_top:
                final Bundle redditQueryParamsTop = new Bundle();
                redditQueryParamsTop.putInt(Reddit.Filter.NODE_DEPTH, 0);
                redditQueryParamsTop.putString(Reddit.Filter.SUBREDDIT_NAME, subreddit);

                postsFragment.setRequest(REDDIT.query(redditQueryParamsTop, Sort.TOP));
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
