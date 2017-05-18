package com.veyndan.paper.reddit

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.Menu
import android.view.MenuItem
import com.airbnb.deeplinkdispatch.DeepLink
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.api.reddit.network.Sort
import com.veyndan.paper.reddit.databinding.ActivityMainBinding
import com.veyndan.paper.reddit.deeplink.WebDeepLink
import com.veyndan.paper.reddit.post.PostsFragment
import com.veyndan.paper.reddit.util.IntentUtils
import io.reactivex.Single
import retrofit2.Response

@WebDeepLink(
        "/u/{" + MainActivity.DEEP_LINK_USER_NAME + '}',
        "/user/{" + MainActivity.DEEP_LINK_USER_NAME + '}'
)
class MainActivity : BaseActivity() {

    companion object {

        const val DEEP_LINK_USER_NAME: String = "user_name"

        private val REDDIT: Reddit = Reddit(PaperForRedditApp.REDDIT_CREDENTIALS)
    }

    private lateinit var postsFragment: PostsFragment

    private lateinit var subreddit: String

    init {
        RxNavi.observe(this, Event.CREATE)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe { _ ->
                    val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

                    setSupportActionBar(binding.toolbar)

                    postsFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_posts) as PostsFragment

                    val intentExtras: Bundle = IntentUtils.getExtras(intent)

                    val filter: Reddit.Filter = when {
                        intentExtras.isEmpty -> Reddit.Filter(
                                nodeDepth = 0,
                                subredditName = "all")
                        intentExtras.getBoolean(DeepLink.IS_DEEP_LINK, false) -> Reddit.Filter(
                                nodeDepth = 0,
                                userName = intentExtras.getString(DEEP_LINK_USER_NAME),
                                userComments = true,
                                userSubmitted = true)
                        else -> intentExtras.getParcelable(Reddit.FILTER)
                    }

                    subreddit = filter.subredditName

                    val mergedFilters: Single<Response<Thing<Listing>>> = REDDIT.query(filter, Sort.HOT)
                    postsFragment.setRequest(mergedFilters)
                }

        RxNavi.observe(this, Event.ACTIVITY_RESULT)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe { activityResult ->
                    if (activityResult.resultCode() == RESULT_OK) {
                        val code: String = activityResult.data()!!.getStringExtra("code")
                    }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item!!.itemId) {
        R.id.action_account_add -> {
            val intent: Intent = Intent(this, AuthenticationActivity::class.java)
            startActivityForResult(intent, 0)
            true
        }
        R.id.action_filter -> {
            val fragmentManager: FragmentManager = supportFragmentManager
            val filterFragment: FilterFragment = FilterFragment.newInstance()
            filterFragment.show(fragmentManager, "fragment_filter")
            true
        }
        R.id.action_sort_hot -> {
            val filter: Reddit.Filter = Reddit.Filter(
                    nodeDepth = 0,
                    subredditName = subreddit)

            postsFragment.setRequest(REDDIT.query(filter, Sort.HOT))
            true
        }
        R.id.action_sort_new -> {
            val filter: Reddit.Filter = Reddit.Filter(
                    nodeDepth = 0,
                    subredditName = subreddit)

            postsFragment.setRequest(REDDIT.query(filter, Sort.NEW))
            true
        }
        R.id.action_sort_rising -> {
            val filter: Reddit . Filter = Reddit.Filter(
                    nodeDepth = 0,
                    subredditName = subreddit)

            postsFragment.setRequest(REDDIT.query(filter, Sort.RISING))
            true
        }
        R.id.action_sort_controversial -> {
            val filter: Reddit.Filter = Reddit.Filter(
                    nodeDepth = 0,
                    subredditName = subreddit)

            postsFragment.setRequest(REDDIT.query(filter, Sort.CONTROVERSIAL))
            true
        }
        R.id.action_sort_top -> {
            val filter: Reddit.Filter = Reddit.Filter(
                    nodeDepth = 0,
                    subredditName = subreddit)

            postsFragment.setRequest(REDDIT.query(filter, Sort.TOP))
            true
        }
        else -> false
    }
}
