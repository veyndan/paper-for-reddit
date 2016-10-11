package com.veyndan.redditclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.network.QueryBuilder;
import com.veyndan.redditclient.api.reddit.network.Sort;
import com.veyndan.redditclient.api.reddit.network.TimePeriod;
import com.veyndan.redditclient.api.reddit.network.User;
import com.veyndan.redditclient.post.PostsFragment;
import com.veyndan.redditclient.post.model.Post;

import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
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

        final Observable<Response<Thing<Listing>>> defaultRequest = Request.subreddit("all", Sort.HOT);
        final Observable<Response<Thing<Listing>>> mergedFilters = mergeFilters(intent.getExtras(), defaultRequest);
        postsFragment.setRequest(mergedFilters);

        final PostsFragment commentsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_comments);

        EventBus.INSTANCE.toObserverable()
                .subscribeOn(Schedulers.io())
                .ofType(Post.class)
                .filter(post -> commentsFragment != null && commentsFragment.isVisible())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(post -> {
                    final String fullname = post.getFullname();
                    final String article = fullname.substring(3, fullname.length());

                    commentsFragment.setRequest(Request.comments(subreddit, article));
                }, Timber::e);
    }

    @NonNull
    private Observable<Response<Thing<Listing>>> mergeFilters(@Nullable final Bundle bundle,
                                                              @NonNull final Observable<Response<Thing<Listing>>> defaultRequest) {
        if (bundle == null) {
            return defaultRequest;
        }

        subreddit = bundle.getString(Filter.SUBREDDIT_NAME);

        final String username = bundle.getString(Filter.USER_NAME);
        final boolean comments = bundle.getBoolean(Filter.USER_COMMENTS);
        final boolean submitted = bundle.getBoolean(Filter.USER_SUBMITTED);
        final boolean gilded = bundle.getBoolean(Filter.USER_GILDED);

        if (subreddit == null && username == null) {
            return defaultRequest;
        } else if (subreddit != null && username == null) {
            return Request.subreddit(subreddit, Sort.HOT);
        } else if (subreddit == null) { // && username != null
            final User user;
            if ((comments == submitted) && gilded) {
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

            return Request.user(username, user);
        } else { // subreddit != null && username != null
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
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.HOT));
                return true;
            case R.id.action_sort_new:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.NEW));
                return true;
            case R.id.action_sort_rising:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.RISING));
                return true;
            case R.id.action_sort_controversial_hour:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.HOUR)));
                return true;
            case R.id.action_sort_controversial_day:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.DAY)));
                return true;
            case R.id.action_sort_controversial_week:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.WEEK)));
                return true;
            case R.id.action_sort_controversial_month:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.MONTH)));
                return true;
            case R.id.action_sort_controversial_year:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.YEAR)));
                return true;
            case R.id.action_sort_controversial_all:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.CONTROVERSIAL, new QueryBuilder().t(TimePeriod.ALL)));
                return true;
            case R.id.action_sort_top_hour:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.HOUR)));
                return true;
            case R.id.action_sort_top_day:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.DAY)));
                return true;
            case R.id.action_sort_top_week:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.WEEK)));
                return true;
            case R.id.action_sort_top_month:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.MONTH)));
                return true;
            case R.id.action_sort_top_year:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.YEAR)));
                return true;
            case R.id.action_sort_top_all:
                postsFragment.setRequest(Request.subreddit(subreddit, Sort.TOP, new QueryBuilder().t(TimePeriod.ALL)));
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
