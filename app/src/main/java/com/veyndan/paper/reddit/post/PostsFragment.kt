package com.veyndan.paper.reddit.post

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.veyndan.paper.reddit.NextPageEvent
import com.veyndan.paper.reddit.NextPageUiModel
import com.veyndan.paper.reddit.PaperForRedditApp
import com.veyndan.paper.reddit.R
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.More
import com.veyndan.paper.reddit.api.reddit.model.Submission
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.databinding.FragmentPostsBinding
import com.veyndan.paper.reddit.post.media.mutator.Mutators
import com.veyndan.paper.reddit.post.model.Post
import com.veyndan.paper.reddit.post.model.Progress
import com.veyndan.paper.reddit.ui.recyclerview.SwipeItemTouchHelperCallback
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.MarginItemDecoration
import com.veyndan.paper.reddit.ui.recyclerview.itemdecoration.TreeInsetItemDecoration
import com.veyndan.paper.reddit.util.Node
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber

class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding

    private val forest: MutableList<Node<Response<Thing<Listing>>>> = ArrayList()

    private lateinit var postAdapter: PostAdapter

    private val reddit = Reddit(PaperForRedditApp.REDDIT_CREDENTIALS)

    init {
        // Required empty public constructor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private fun endOfRecyclerView(): Predicate<RecyclerViewScrollEvent> {
        return Predicate { scrollEvent ->
            // Scroll down: scrollEvent.dy() > 0
            // Initial load: scrollEvent.dy() == 0
            if (scrollEvent.dy() >= 0) {
                val recyclerView = scrollEvent.view()
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val visibleItemCount = recyclerView.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                return@Predicate totalItemCount - visibleItemCount <= firstVisibleItem
            }
            return@Predicate false
        }
    }

    fun setRequest(request: Single<Response<Thing<Listing>>>) {
        clearForest()

        val nextPageEvents: Observable<NextPageEvent> = RxRecyclerView.scrollEvents(binding.recyclerView)
                .filter(endOfRecyclerView())
                // TODO There should be a more robust and intuitive way of doing distinctUntilChanged().
                //
                //      When to solve: After nodes is no longer a flattened tree but is instead a
                //      indexable tree.
                .map { _ -> NextPageEvent(forest.last()) }
                .distinctUntilChanged { _ -> forest.lastIndex }

        val nextPage: ObservableTransformer<NextPageEvent, NextPageUiModel> = ObservableTransformer { events ->
            events
                    .flatMapSingle { _ ->
                        request
                                .subscribeOn(Schedulers.io())
                                .map { response -> response.body()!! }
                                .flattenAsObservable { thing -> thing!!.data.children }
                                .observeOn(Schedulers.computation())
                                .flatMapSingle { redditObject ->
                                    if (redditObject is Submission) {
                                        Single.just(redditObject)
                                                .map { submission -> Post.create(submission) }
                                                .flatMap(Mutators.mutate())
                                    } else if (redditObject is More) {
                                        Single.just(Progress(redditObject.count))
                                    } else {
                                        val message = "Unknown node class: " + redditObject
                                        Single.error(IllegalStateException(message))
                                    }
                                }
                                .concatWith(Observable.just(Progress()))
                                .observeOn(AndroidSchedulers.mainThread())
                                .concatMap { tree1 -> tree1.preOrderTraverse(0) }
                                .toList()
                    }
                    .map { forest1 -> NextPageUiModel(forest1) }
                    .startWith(NextPageUiModel(Progress()))
        }

        nextPageEvents.compose(nextPage)
                .subscribe { model ->
                    // TODO Logic: The below assumes that the last element is the one to be replaced (i.e. event.getNode())
                    // though it should allow any node i.e. for the comment section.
                    Timber.d("Next page")
                    if (forest.isNotEmpty()) { // TODO Code smell: This is done as startWith is called above.
                        popTree()
                    }
                    appendForest(model.forest)
                }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        val layoutManager: LinearLayoutManager = LinearLayoutManager(activity)
        postAdapter = PostAdapter(activity, forest, reddit)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(MarginItemDecoration(activity, R.dimen.card_view_margin))
        binding.recyclerView.addItemDecoration(TreeInsetItemDecoration(activity, R.dimen.post_child_inset_multiplier))
        binding.recyclerView.adapter = postAdapter

        val swipeCallback = SwipeItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    fun appendTree(node: Node<Response<Thing<Listing>>>) {
        forest.add(node)
        postAdapter.notifyItemInserted(forest.lastIndex)
    }

    fun appendForest(forest: List<Node<Response<Thing<Listing>>>>) {
        val positionStart = this.forest.size
        this.forest.addAll(forest)
        postAdapter.notifyItemRangeInserted(positionStart, forest.size)
    }

    fun popTree(): Node<Response<Thing<Listing>>> = popTree(forest.lastIndex)

    fun popTree(index: Int): Node<Response<Thing<Listing>>> {
        val poppedTree: Node<Response<Thing<Listing>>> = forest[index]
        forest.removeAt(index)
        postAdapter.notifyItemRemoved(index)
        return poppedTree
    }

    fun clearForest() {
        val treeCount = forest.size
        forest.clear()
        postAdapter.notifyItemRangeRemoved(0, treeCount)
    }
}
