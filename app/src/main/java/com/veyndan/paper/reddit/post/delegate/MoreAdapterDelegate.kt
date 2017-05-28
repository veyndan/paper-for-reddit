package com.veyndan.paper.reddit.post.delegate

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.veyndan.paper.reddit.R
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.databinding.MoreItemBinding
import com.veyndan.paper.reddit.post.model.Progress
import com.veyndan.paper.reddit.util.Node
import retrofit2.Response

class MoreAdapterDelegate : AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, MoreAdapterDelegate.MoreViewHolder>() {

    override fun isForViewType(node: Node<Response<Thing<Listing>>>,
                               nodes: MutableList<Node<Response<Thing<Listing>>>>,
                               position: Int): Boolean {
        return node is Progress && node.degree().count().blockingGet() > 0
    }

    override fun onCreateViewHolder(parent: ViewGroup): MoreViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: MoreItemBinding = MoreItemBinding.inflate(inflater, parent, false)
        return MoreViewHolder(binding)
    }

    override fun onBindViewHolder(progress: Progress, holder: MoreViewHolder, payloads: List<Any>) {
        val count: Int = progress.degree().blockingGet().toInt()
        val resources: Resources = holder.itemView.resources

        holder.binding.moreText.text = resources.getQuantityString(R.plurals.children, count, count)
    }

    class MoreViewHolder(val binding: MoreItemBinding) : RecyclerView.ViewHolder(binding.root)
}
