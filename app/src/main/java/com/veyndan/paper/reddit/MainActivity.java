package com.veyndan.paper.reddit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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
import com.veyndan.paper.reddit.databinding.ActivityMainBinding;
import com.veyndan.paper.reddit.post.PostsFragment;
import com.veyndan.paper.reddit.util.IntentUtils;

import java.io.IOException;

import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

@DeepLink({
        "http://reddit.com/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "http://reddit.com/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    static final String DEEP_LINK_USER_NAME = "user_name";

    // TODO BAD. No public static
    public static Reddit REDDIT;

    private PostsFragment postsFragment;

    private String subreddit;

    // TODO Bad design for this here
    private Bundle intentExtras;

    @Override
    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // TODO This is temporary but better name it. This means that authentication is always required. Will cache or use AccountManager so don't have to sign in each time in future commit.
        final Intent intent1 = new Intent(this, AuthenticationActivity.class);
        startActivityForResult(intent1, 0);

        setSupportActionBar(binding.toolbar);

        final AccountManager accountManager = AccountManager.get(this);
        final Bundle options = new Bundle();

        final Account[] accounts = accountManager.getAccountsByType("com.veyndan");

        accountManager.getAuthToken(
                accounts[0],
                TextUtils.join(",", AuthenticationActivity.SCOPES),
                options,
                this,
                accountManagerFuture -> {
                    try {
                        final Bundle bundle = accountManagerFuture.getResult();
                        final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN, "");
                        Timber.d("token=%s", token);
                    } catch (final OperationCanceledException | IOException | AuthenticatorException e) {
                        Timber.e(e);
                    }
                },
                new Handler(msg -> {
                    throw new IllegalStateException("Something happend");
                })
        );

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

        this.intentExtras = intentExtras;
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final String code = data.getStringExtra("code");

            // TODO code retrieval has to be improved, REDDIT initialization shouldn't be here.
            REDDIT = new Reddit(Config.REDDIT_CREDENTIALS, code);

            // TODO No reason for this here except that REDDIT is now non null
            final Single<Response<Thing<Listing>>> mergedFilters = REDDIT.query(intentExtras, Sort.HOT);
            postsFragment.setRequest(mergedFilters);
        }
    }
}
