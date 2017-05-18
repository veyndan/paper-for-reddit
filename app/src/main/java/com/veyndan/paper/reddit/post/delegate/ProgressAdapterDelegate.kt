package com.veyndan.paper.reddit.post.delegate

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.veyndan.paper.reddit.api.reddit.model.Listing
import com.veyndan.paper.reddit.api.reddit.model.Thing
import com.veyndan.paper.reddit.databinding.ProgressItemBinding
import com.veyndan.paper.reddit.post.model.Progress
import com.veyndan.paper.reddit.util.Node
import retrofit2.Response

class ProgressAdapterDelegate
    : AbsListItemAdapterDelegate<Progress, Node<Response<Thing<Listing>>>, ProgressAdapterDelegate.ProgressViewHolder>() {

    override fun isForViewType(node: Node<Response<Thing<Listing>>>, nodes: MutableList<Node<Response<Thing<Listing>>>>, position: Int): Boolean {
        return node is Progress && node.degree == null
    }

    override fun onCreateViewHolder(parent: ViewGroup): ProgressViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ProgressItemBinding = ProgressItemBinding.inflate(inflater, parent, false)
        return ProgressViewHolder(binding)
    }

    override fun onBindViewHolder(item: Progress, viewHolder: ProgressViewHolder, payloads: MutableList<Any>) {
    }

    class ProgressViewHolder(val binding: ProgressItemBinding) : RecyclerView.ViewHolder(binding.root)
}
