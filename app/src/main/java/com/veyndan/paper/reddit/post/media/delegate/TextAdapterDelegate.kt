package com.veyndan.paper.reddit.post.media.delegate

import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate
import com.veyndan.paper.reddit.databinding.PostMediaTextBinding
import com.veyndan.paper.reddit.post.media.model.Text

class TextAdapterDelegate : AbsListItemAdapterDelegate<Text, Any, TextAdapterDelegate.TextViewHolder>() {

    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Text
    }

    override fun onCreateViewHolder(parent: ViewGroup): TextViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: PostMediaTextBinding = PostMediaTextBinding.inflate(inflater, parent, false)
        return TextViewHolder(binding)
    }

    override fun onBindViewHolder(text: Text, holder: TextViewHolder, payloads: List<Any>) {
        val context = holder.itemView.context
        holder.binding.postMediaText.text = text.body.invoke(context)
        holder.binding.postMediaText.movementMethod = LinkMovementMethod.getInstance()
    }

    class TextViewHolder(val binding: PostMediaTextBinding) : RecyclerView.ViewHolder(binding.root)
}
