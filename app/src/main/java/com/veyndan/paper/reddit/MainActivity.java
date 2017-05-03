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

@WebDeepLink({
        "/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    static final String DEEP_LINK_USER_NAME = "user_name";

    private static final Reddit REDDIT = new Reddit(PaperForRedditApp.REDDIT_CREDENTIALS);

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

                    final Reddit.Filter filter;

                    if (intentExtras.isEmpty()) {
                        filter = Reddit.Filter.builder()
                                .nodeDepth(0)
                                .subredditName("all")
                                .build();
                    } else if (intentExtras.getBoolean(DeepLink.IS_DEEP_LINK, false)) {
                        filter = Reddit.Filter.builder()
                                .nodeDepth(0)
                                .userName(intentExtras.getString(DEEP_LINK_USER_NAME))
                                .userComments(true)
                                .userSubmitted(true)
                                .build();
                    } else {
                        filter = intentExtras.getParcelable(Reddit.FILTER);
                    }

                    subreddit = filter.subredditName();

                    final Single<Response<Thing<Listing>>> mergedFilters = REDDIT.query(filter, Sort.HOT);
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
                final Reddit.Filter filterHot = Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(filterHot, Sort.HOT));
                return true;
            case R.id.action_sort_new:
                final Reddit.Filter filterNew = Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(filterNew, Sort.NEW));
                return true;
            case R.id.action_sort_rising:
                final Reddit.Filter filterRising = Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(filterRising, Sort.RISING));
                return true;
            case R.id.action_sort_controversial:
                final Reddit.Filter filterControversial = Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(filterControversial, Sort.CONTROVERSIAL));
                return true;
            case R.id.action_sort_top:
                final Reddit.Filter filterTop = Reddit.Filter.builder()
                        .nodeDepth(0)
                        .subredditName(subreddit)
                        .build();

                postsFragment.setRequest(REDDIT.query(filterTop, Sort.TOP));
                return true;
            default:
                return false;
        }
    }
}
