package com.veyndan.redditclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import rawjava.model.Thing;
import rawjava.model.Trophy;

public class TrophyAdapter extends RecyclerView.Adapter<TrophyAdapter.TrohpyViewHolder> {

    private final Activity activity;
    private final List<Thing<Trophy>> trophies;
    private final CustomTabsClient customTabsClient;
    private final CustomTabsIntent customTabsIntent;

    public TrophyAdapter(Activity activity, List<Thing<Trophy>> trophies, @Nullable CustomTabsClient customTabsClient, CustomTabsIntent customTabsIntent) {
        this.activity = activity;
        this.trophies = trophies;
        this.customTabsClient = customTabsClient;
        this.customTabsIntent = customTabsIntent;
    }

    @Override
    public TrohpyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_trophy, parent, false);
        return new TrohpyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrohpyViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final Thing<Trophy> trophy = trophies.get(position);

        holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        Glide.with(context).load(trophy.data.icon70).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                holder.text.setCompoundDrawablesWithIntrinsicBounds(null, glideDrawable, null, null);
            }
        });
        final SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append(trophy.data.name, new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(trophy.data.description)) {
            spannable.append("\n");
            spannable.append(trophy.data.description, new StyleSpan(Typeface.NORMAL), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.text.setText(spannable);

        if (customTabsClient != null) {
            CustomTabsSession session = customTabsClient.newSession(null);
            session.mayLaunchUrl(Uri.parse(trophy.data.url), null, null);
        }

        RxView.clicks(holder.itemView)
                .subscribe(aVoid -> {
                    if (!TextUtils.isEmpty(trophy.data.url)) {
                        customTabsIntent.launchUrl(activity, Uri.parse(trophy.data.url));
                    }
                });
    }

    @Override
    public int getItemCount() {
        return trophies.size();
    }

    public class TrohpyViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        public TrohpyViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView;
        }
    }
}
