package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.trello.navi2.Event;
import com.trello.navi2.rx.RxNavi;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.model.Listing;
import com.veyndan.paper.reddit.api.reddit.model.Thing;
import com.veyndan.paper.reddit.api.reddit.network.Sort;
import com.veyndan.paper.reddit.databinding.ActivityMainBinding;
import com.veyndan.paper.reddit.deeplink.WebDeepLink;
import com.veyndan.paper.reddit.post.PostsFragment;
import com.veyndan.paper.reddit.util.IntentUtils;

import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

@WebDeepLink({
        "/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    static final String DEEP_LINK_USER_NAME = "user_name";

    private static final Reddit REDDIT = new Reddit(Config.REDDIT_CREDENTIALS);

    private PostsFragment postsFragment;

    private String subreddit;

    public MainActivity() {
        RxNavi.observe(this, Event.CREATE)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe(savedInstanceState -> {
                    final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

                    setSupportActionBar(binding.toolbar);

                    postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

                    final Intent intent = getIntent();
                    final Bundle intentExtras = IntentUtils.getExtras(intent);

                    if (intentExtras.isEmpty()) {
                        intentExtras.putAll(new Reddit.FilterBuilder()
                                .nodeDepth(0)
                                .subredditName("all")
                                .build());
                    } else if (intentExtras.getBoolean(DeepLink.IS_DEEP_LINK, false)) {
                        intentExtras.putAll(new Reddit.FilterBuilder()
                                .nodeDepth(0)
                                .userName(intentExtras.getString(DEEP_LINK_USER_NAME))
                                .userComments(true)
                                .userSubmitted(true)
                                .build());
                    }

                    subreddit = intentExtras.getString(Reddit.FILTER_SUBREDDIT_NAME);

                    final Single<Response<Thing<Listing>>> mergedFilters = REDDIT.query(intentExtras, Sort.HOT);
                    postsFragment.setRequest(mergedFilters);
                });

        RxNavi.observe(this, Event.ACTIVITY_RESULT)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe(activityResult -> {
                    if (activityResult.resultCode() == RESULT_OK) {
                        final String code = activityResult.data().getStringExtra("code");
                    }
                });
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
                final Bundle redditQueryParamsHot = new Reddit.FilterBuilder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(redditQueryParamsHot, Sort.HOT));
                return true;
            case R.id.action_sort_new:
                final Bundle redditQueryParamsNew = new Reddit.FilterBuilder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(redditQueryParamsNew, Sort.NEW));
                return true;
            case R.id.action_sort_rising:
                final Bundle redditQueryParamsRising = new Reddit.FilterBuilder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(redditQueryParamsRising, Sort.RISING));
                return true;
            case R.id.action_sort_controversial:
                final Bundle redditQueryParamsControversial = new Reddit.FilterBuilder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(redditQueryParamsControversial, Sort.CONTROVERSIAL));
                return true;
            case R.id.action_sort_top:
                final Bundle redditQueryParamsTop = new Reddit.FilterBuilder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(redditQueryParamsTop, Sort.TOP));
                return true;
            default:
                return false;
        }
    }
}
