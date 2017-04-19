package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.google.common.collect.ImmutableList;
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
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import retrofit2.Response;
import timber.log.Timber;

@WebDeepLink({
        "/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    static final String DEEP_LINK_USER_NAME = "user_name";

    private static final Reddit REDDIT = new Reddit(Config.REDDIT_CREDENTIALS);

    private final Subject<MenuItem> optionsItemSelected = PublishSubject.create();

    public MainActivity() {
        RxNavi.observe(this, Event.CREATE)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe(savedInstanceState -> {
                    final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

                    setSupportActionBar(binding.toolbar);

                    final PostsFragment postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

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

                    final String subreddit = intentExtras.getString(Reddit.FILTER_SUBREDDIT_NAME);

                    final Single<Response<Thing<Listing>>> mergedFilters = REDDIT.query(intentExtras, Sort.HOT);
                    postsFragment.setRequest(mergedFilters);

                    // TODO I can't see any reason why onOptionsItemSelected would be called before onCreate or after onDestroy so this should be valid but find some documentation to back up intuition.
                    // TODO Not very elegant subscribing to an Observable inside an Observable. Also this is called in onNext() remember so potentially called more than once (though I can't see why).
                    // TODO Also the reason why we are doing this is to reduce the scope of PostsFragment so we know it is non null. We haven't changed the logic of the code to ensure that it is non null so maybe my first TODO assumption is incorrect.
                    optionsItemSelected.subscribe(menuItem -> {
                        switch (menuItem.getItemId()) {
                            case R.id.action_account_add:
                                final Intent addAccountIntent = new Intent(this, AuthenticationActivity.class);
                                startActivityForResult(addAccountIntent, 0);
                                break;
                            case R.id.action_filter:
                                final FragmentManager fragmentManager = getSupportFragmentManager();
                                final FilterFragment filterFragment = FilterFragment.newInstance();
                                filterFragment.show(fragmentManager, "fragment_filter");
                                break;
                            case R.id.action_sort_hot:
                                final Bundle redditQueryParamsHot = new Reddit.FilterBuilder()
                                        .nodeDepth(0)
                                        .subredditName(subreddit)
                                        .build();

                                postsFragment.setRequest(REDDIT.query(redditQueryParamsHot, Sort.HOT));
                                break;
                            case R.id.action_sort_new:
                                final Bundle redditQueryParamsNew = new Reddit.FilterBuilder()
                                        .nodeDepth(0)
                                        .subredditName(subreddit)
                                        .build();

                                postsFragment.setRequest(REDDIT.query(redditQueryParamsNew, Sort.NEW));
                                break;
                            case R.id.action_sort_rising:
                                final Bundle redditQueryParamsRising = new Reddit.FilterBuilder()
                                        .nodeDepth(0)
                                        .subredditName(subreddit)
                                        .build();

                                postsFragment.setRequest(REDDIT.query(redditQueryParamsRising, Sort.RISING));
                                break;
                            case R.id.action_sort_controversial:
                                final Bundle redditQueryParamsControversial = new Reddit.FilterBuilder()
                                        .nodeDepth(0)
                                        .subredditName(subreddit)
                                        .build();

                                postsFragment.setRequest(REDDIT.query(redditQueryParamsControversial, Sort.CONTROVERSIAL));
                                break;
                            case R.id.action_sort_top:
                                final Bundle redditQueryParamsTop = new Reddit.FilterBuilder()
                                        .nodeDepth(0)
                                        .subredditName(subreddit)
                                        .build();

                                postsFragment.setRequest(REDDIT.query(redditQueryParamsTop, Sort.TOP));
                                break;
                            default:
                                break;
                        }
                    });
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
        final ImmutableList<Integer> consumableOptionsItemIds = new ImmutableList.Builder<Integer>()
                .add(R.id.action_account_add)
                .add(R.id.action_filter)
                .add(R.id.action_sort_hot)
                .add(R.id.action_sort_new)
                .add(R.id.action_sort_rising)
                .add(R.id.action_sort_controversial)
                .add(R.id.action_sort_top)
                .build();

        final boolean consumed = consumableOptionsItemIds.contains(item.getItemId());

        if (consumed) {
            optionsItemSelected.onNext(item);
        }

        return consumed;
    }
}
