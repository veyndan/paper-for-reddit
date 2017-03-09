package com.veyndan.paper.reddit.post.media.delegate;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.jakewharton.rxbinding2.view.RxView;
import com.veyndan.paper.reddit.databinding.PostMediaLinkImageBinding;
import com.veyndan.paper.reddit.image.ImageLoader;
import com.veyndan.paper.reddit.image.imp.CustomDecoder;
import com.veyndan.paper.reddit.image.imp.CustomNetwork;
import com.veyndan.paper.reddit.post.media.model.LinkImage;
import com.veyndan.paper.reddit.post.model.Post;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LinkImageAdapterDelegate
        extends AbsListItemAdapterDelegate<LinkImage, Object, LinkImageAdapterDelegate.LinkImageViewHolder> {

    private final Activity activity;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;
    private final Post post;

    public LinkImageAdapterDelegate(final Activity activity, final CustomTabsClient customTabsClient,
                                    final CustomTabsIntent customTabsIntent, final Post post) {
        this.activity = activity;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
        this.post = post;
    }

    @Override
    protected boolean isForViewType(@NonNull final Object item, @NonNull final List<Object> items,
                                    final int position) {
        return item instanceof LinkImage;
    }

    @NonNull
    @Override
    public LinkImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PostMediaLinkImageBinding binding = PostMediaLinkImageBinding.inflate(inflater, parent, false);
        return new LinkImageViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull final LinkImage linkImage,
                                    @NonNull final LinkImageViewHolder holder,
                                    @NonNull final List<Object> payloads) {
        final Context context = holder.itemView.getContext();

        if (customTabsClient != null) {
            final CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(post.getLinkUrl()), null, null);
        }

        RxView.clicks(holder.itemView)
                .subscribe(aVoid -> {
                    customTabsIntent.launchUrl(activity, Uri.parse(post.getLinkUrl()));
                }, Timber::e);

        holder.binding.postMediaImageProgress.setVisibility(View.VISIBLE);

        Single.just(linkImage.getUrl())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(url -> ImageLoader.load(url, context, new CustomNetwork().getImageAsInputStream(url)
                        .subscribeOn(Schedulers.io())
                        .map(inputStream -> new CustomDecoder().decodeInputStream(inputStream))))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> holder.binding.postMediaImageProgress.setVisibility(View.GONE))
                .subscribe(bitmap -> {
                    Timber.d("SUC %s", linkImage.getUrl());
                    holder.binding.postMediaImage.setImageBitmap(bitmap);
                }, throwable -> {
                    Timber.d("FAI %s", linkImage.getUrl());
                });

        holder.binding.postMediaUrl.setText(linkImage.getDomain());
    }

    static class LinkImageViewHolder extends RecyclerView.ViewHolder {

        private final PostMediaLinkImageBinding binding;

        LinkImageViewHolder(final PostMediaLinkImageBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
