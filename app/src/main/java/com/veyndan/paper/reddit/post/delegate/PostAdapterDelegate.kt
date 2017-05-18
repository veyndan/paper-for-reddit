package com.veyndan.paper.reddit.post.delegate

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.jakewharton.rxbinding2.support.design.widget.dismisses
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemClicks
import com.veyndan.paper.reddit.MainActivity
import com.veyndan.paper.reddit.R
import com.veyndan.paper.reddit.api.reddit.Reddit
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.api.reddit.network.VoteDirection
import com.veyndan.paper.reddit.databinding.PostItemBinding
import com.veyndan.paper.reddit.post.Flair
import com.veyndan.paper.reddit.post.PostAdapter
import com.veyndan.paper.reddit.post.media.PostMediaAdapter
import com.veyndan.paper.reddit.post.model.Post
import com.veyndan.paper.reddit.ui.recyclerview.Swipeable
import com.veyndan.paper.reddit.util.Node
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class PostAdapterDelegate(private val adapter: PostAdapter, private val activity: Activity,
                          private val reddit: Reddit)
    : AdapterDelegate<MutableList<Node<Response<Thing<Listing>>>>>() {

    companion object {

        private const val CUSTOM_TAB_PACKAGE_NAME: String = "com.android.chrome"
    }

    private val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
    private val customTabsIntent: CustomTabsIntent = builder.build()
    private var customTabsClient: CustomTabsClient? = null

    init {
        CustomTabsClient.bindCustomTabsService(activity, CUSTOM_TAB_PACKAGE_NAME, object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
                // customTabsClient is now valid.
                customTabsClient = client
                customTabsClient!!.warmup(0)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // customTabsClient is no longer valid. This also invalidates sessions.
                customTabsClient = null
            }
        })
    }

    override fun isForViewType(nodes: MutableList<Node<Response<Thing<Listing>>>>, position: Int): Boolean = nodes[position] is Post

    override fun onCreateViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent!!.context)
        val binding: PostItemBinding = PostItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding, adapter, reddit)
    }

    override fun onBindViewHolder(nodes: MutableList<Node<Response<Thing<Listing>>>>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {
        val context: Context = holder.itemView.context
        val postHolder: PostViewHolder = holder as PostViewHolder
        val post: Post = nodes[position] as Post

        if (payloads.isEmpty()) {
            bindTitle(post, postHolder)
            bindSubtitle(post, postHolder)
            bindFlairs(context, post, postHolder)
            bindMedia(post, postHolder, activity, customTabsClient, customTabsIntent)
            bindPoints(context, post, postHolder)
            bindActions(context, nodes, post, postHolder, reddit, adapter)
        } else if (payloads[0] == PostPayload.VOTE) {
            bindPoints(context, post, postHolder)
        }
    }

    private fun bindTitle(post: Post, holder: PostViewHolder) {
        holder.binding.postTitle.setTitle(post.linkTitle!!)
    }

    private fun bindSubtitle(post: Post, holder: PostViewHolder) {
        holder.binding.postSubtitle.setSubtitle(post.author, post.getDisplayAge(), post.subreddit)
    }

    private fun bindFlairs(context: Context, post: Post, holder: PostViewHolder) {
        val flairs: Collection <Flair> = post.flairs(context)
        holder.binding.postFlairs.setFlairs(flairs, post.subreddit)
    }

    private fun bindMedia(post: Post, holder: PostViewHolder, activity: Activity,
                          customTabsClient: CustomTabsClient?, customTabsIntent: CustomTabsIntent) {
        post.medias
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe { medias ->
                    val postMediaAdapter: PostMediaAdapter = PostMediaAdapter(
                            activity, customTabsClient, customTabsIntent, post, medias)
                    holder.binding.postMediaView.adapter = postMediaAdapter
                }
    }

    private fun bindPoints(context: Context, post: Post, holder: PostViewHolder) {
        val points: String = post.getDisplayPoints(context)
        holder.binding.postScore.text = points
    }

    private fun bindActions(context: Context, nodes: MutableList<Node<Response<Thing<Listing>>>>,
                            post: Post, holder: PostViewHolder, reddit: Reddit,
                            adapter: PostAdapter) {
        bindUpvoteAction(post, holder, reddit, adapter)
        bindDownvoteAction(post, holder, reddit, adapter)
        bindSaveAction(post, holder, reddit)
        bindCommentsAction(context, nodes, post, holder, adapter)
        bindPopupActions(context, post, holder)
    }

    private fun bindUpvoteAction(post: Post, holder: PostViewHolder, reddit: Reddit,
                                 adapter: PostAdapter) {
        val likes: VoteDirection = post.likes
        holder.binding.postUpvoteNew.isChecked = likes == VoteDirection.UPVOTE
        holder.binding.postUpvoteNew.checkedChanges()
                // checkedChanges emits the checked state on subscription. As the voted state of
                // the Reddit post is the same as the checked state of the button initially,
                // skipping the initial emission means no unnecessary network requests occur.
                .skip(1)
                .filter { !post.archived }
                .subscribe { isChecked ->
                    post.likes = if (isChecked) VoteDirection.UPVOTE else VoteDirection.UNVOTE
                    reddit.vote(post.likes, post.fullname)
                            .subscribeOn(Schedulers.io())
                            .subscribe()

                    post.points += if (isChecked) 1 else -1

                    adapter.notifyItemChanged(holder.adapterPosition, PostPayload.VOTE)
                }
    }

    private fun bindDownvoteAction(post: Post, holder: PostViewHolder, reddit: Reddit,
                                   adapter: PostAdapter) {
        val likes: VoteDirection = post.likes
        holder.binding.postDownvoteNew.isChecked = likes == VoteDirection.DOWNVOTE
        holder.binding.postDownvoteNew.checkedChanges()
                // checkedChanges emits the checked state on subscription. As the voted state of
                // the Reddit post is the same as the checked state of the button initially,
                // skipping the initial emission means no unnecessary network requests occur.
                .skip(1)
                .filter { !post.archived }
                .subscribe { isChecked ->
                    post.likes = if (isChecked) VoteDirection.DOWNVOTE else VoteDirection.UNVOTE
                    reddit.vote(post.likes, post.fullname)
                            .subscribeOn(Schedulers.io())
                            .subscribe()

                    post.points += if (isChecked) -1 else 1

                    adapter.notifyItemChanged(holder.adapterPosition, PostPayload.VOTE)
                }
    }

    private fun bindSaveAction(post: Post, holder: PostViewHolder, reddit: Reddit) {
        holder.binding.postSave.isChecked = post.saved
        holder.binding.postSave.clicks()
                .subscribe {
                    holder.binding.postSave.toggle()
                    val isChecked: Boolean = holder.binding.postSave.isChecked

                    post.saved = isChecked
                    if (isChecked) {
                        reddit.save("", post.fullname)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    } else {
                        reddit.unsave(post.fullname)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    }
                }
    }

    private fun bindCommentsAction(context: Context, nodes: MutableList<Node<Response<Thing<Listing>>>>, post: Post, holder: PostViewHolder, adapter: PostAdapter) {
        holder.binding.postComments.clicks()
                .map {
                    holder.binding.postComments.toggle()
                    holder.binding.postComments.isChecked
                }
                .subscribe { displayDescendants ->
                    post.descendantsVisible = !displayDescendants
                    if (post.comment) {
                        if (displayDescendants) {
                            var i: Int = holder.adapterPosition + 1
                            while (i < nodes.size && nodes[i].depth > post.depth) {
                                i++
                            }

                            nodes.subList(holder.adapterPosition + 1, i).clear()
                            adapter.notifyItemRangeRemoved(holder.adapterPosition + 1, i - (holder.adapterPosition + 1))

                            holder.binding.postCommentCount.visibility = View.VISIBLE
                            holder.binding.postCommentCount.text = (i - (holder.adapterPosition + 1)).toString()
                        } else {
                            post.preOrderTraverse(post.depth)
                                    .skip(1)
                                    .toList()
                                    .subscribe { children ->
                                        nodes.addAll(holder.adapterPosition + 1, children)
                                        adapter.notifyItemRangeInserted(holder.adapterPosition + 1, children.size)
                                    }

                            holder.binding.postCommentCount.visibility = View.INVISIBLE
                        }
                    } else {
                        val commentsIntent: Intent = Intent(context, MainActivity::class.java)
                        commentsIntent.putExtra(Reddit.FILTER, Reddit.Filter(
                                nodeDepth = 0,
                                commentsSubreddit = post.subreddit,
                                commentsArticle = post.article()))
                        context.startActivity(commentsIntent)
                    }
                }

        post.internalNode()
                .subscribe { internalNode ->
                    if (internalNode && !post.descendantsVisible) {
                        holder.binding.postCommentCount.visibility = View.VISIBLE
                        post.getDisplayDescendants()
                                .subscribe { descendants -> holder.binding.postCommentCount.text = descendants }
                    } else {
                        holder.binding.postCommentCount.visibility = View.INVISIBLE
                    }
                }
    }

    private fun bindPopupActions(context: Context, post: Post, holder: PostViewHolder) {
        val otherMenu: PopupMenu = PopupMenu(context, holder.binding.postOther)
        otherMenu.menuInflater.inflate(R.menu.menu_post_other, otherMenu.menu)

        holder.binding.postOther.clicks()
                .subscribe { otherMenu.show() }

        otherMenu.itemClicks()
                .subscribe { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_post_share -> bindShareAction(context, post)
                        R.id.action_post_browser -> bindBrowserAction(context, post)
                        R.id.action_post_report -> {
                        }
                    }
                }
    }

    private fun bindShareAction(context: Context, post: Post) {
        val intent: Intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, post.permalink)
        intent.type = "text/plain"
        context.startActivity(intent)
    }

    private fun bindBrowserAction(context: Context, post: Post) {
        val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkUrl))
        context.startActivity(intent)
    }

    private enum class PostPayload {
        VOTE
    }

    class PostViewHolder(val binding: PostItemBinding, private val adapter: PostAdapter,
                         private val reddit: Reddit)
        : RecyclerView.ViewHolder(binding.root), Swipeable {

        override fun swipeable(): Boolean {
            val post: Post = adapter.items[adapterPosition] as Post
            return post.hideable
        }

        override fun onSwipe() {
            val position: Int = adapterPosition
            val node: Node <Response <Thing <Listing>>> = adapter.items[position]
            val post: Post = node as Post

            val undoClickListener: View.OnClickListener = View.OnClickListener {
                // If undo pressed, then don't follow through with request to hide
                // the post.
                adapter.items.add(position, node)
                adapter.notifyItemInserted(position)
            }

            val snackbar: Snackbar = Snackbar.make(itemView, R.string.notify_post_hidden, Snackbar.LENGTH_LONG)
                    .setAction(R.string.notify_post_hidden_undo, undoClickListener)

            snackbar.dismisses()
                    // If undo pressed, don't hide post.
                    .filter { event -> event != Snackbar.Callback.DISMISS_EVENT_ACTION }
                    .firstElement()
                    .subscribe {
                        // Chance to undo post hiding has gone, so follow through with
                        // hiding network request.
                        reddit.hide(post.fullname)
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                    }

            snackbar.show()

            // Hide post from list, but make no network request yet. Outcome of the
            // user's interaction with the snackbar handling will determine this.
            adapter.items.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
}
