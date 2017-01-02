package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import timber.log.Timber;

@DeepLink({
        Constants.REDDIT_REDIRECT_URI,
        "http://reddit.com/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "http://reddit.com/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
})
public class MainActivity extends BaseActivity {

    static final String DEEP_LINK_USER_NAME = "user_name";

    private static final String ERROR_ACCESS_DENIED = "access_denied";
    private static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";
    private static final String ERROR_INVALID_REQUEST = "invalid_request";

    private static final Reddit REDDIT = new Reddit(Config.REDDIT_CREDENTIALS);

    private PostsFragment postsFragment;

    private String subreddit;

    // TODO This is hidden from this activity
    private static String expectedState;

    @Override
    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);

        postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_posts);

        final Intent intent = getIntent();
        final Bundle intentExtras = IntentUtils.getExtras(intent);

        Timber.d(intentExtras.getString(DeepLink.URI, "Wasn't a deep link"));

        if (intentExtras.getBoolean(DeepLink.IS_DEEP_LINK, false)) {
            final String uri = intentExtras.getString(DeepLink.URI, "");
            if (uri.startsWith(Constants.REDDIT_REDIRECT_URI)) {
                final String returnedState = intentExtras.getString("state", "");
                final String code = intentExtras.getString("code", "");
                final String error = intentExtras.getString("error", "");

                // TODO What if the user opens it in Chrome and modifys the url? They could change
                // the access permissions, which if any of them are deleted could crash the app constantly.
                // Even if I somehow prevent opening it in Chrome, they can still manually copy the
                // link into Chrome and modify it. Changing duration and response_type can also cause problems.
                // Is it required that I show Reddits page or can I parse the HTML and do the login using Retrofit and standard Android views? Probably not allowed to do this

                if (!expectedState.equals(returnedState)) {
                    // TODO Potentially notify user what had happened?
                    Timber.e("This app didn't initiate the authorization request. " +
                            "Authorization request will not be carried out.");
                } else if (!error.isEmpty()) {
                    switch (error) {
                        case ERROR_ACCESS_DENIED:
                            Toast.makeText(this, R.string.login_aborted, Toast.LENGTH_LONG).show();
                            break;
                        case ERROR_UNSUPPORTED_RESPONSE_TYPE:
                            throw new IllegalStateException("Invalid response_type: " +
                                    "Ensure that the response_type parameter is one of the " +
                                    "allowed values");
                        case ERROR_INVALID_SCOPE:
                            throw new IllegalStateException("Invalid scope parameter: " +
                                    "Ensure that the scope parameter is a space-separated " +
                                    "list of valid scopes");
                        case ERROR_INVALID_REQUEST:
                            throw new IllegalStateException("Invalid request: " +
                                    "Double check url parameters");
                        default:
                            throw new IllegalStateException("Unknown error type");
                    }
                } else {
                    // Do something with `code`
                }
            }
        }

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
                expectedState = UserAuthentication.authenticateUser(this);
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
