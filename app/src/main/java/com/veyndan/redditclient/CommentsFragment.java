package com.veyndan.redditclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.PostAdapter;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.TreeInsetItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;

    @BindDimen(R.dimen.post_child_inset_multiplier) int childInsetMultiplier;

    final Reddit reddit = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();

    final List<Tree.Node<Post>> nodes = new ArrayList<>();

    PostAdapter postAdapter;

    TreeInsetItemDecoration treeInsetItemDecoration;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public void setCommentRequest(final Observable<Response<List<Thing<Listing>>>> commentRequest) {
        commentRequest
                .subscribeOn(Schedulers.io())
                .map(Response::body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(things -> {
                    // No data is lost as both things.get(0) and things.get(1), which is all the
                    // things, has null set to before, after, and modhash in the Listing.
                    // things.get(0).data.children contains the Link.java only
                    final Tree<RedditObject> tree = new Tree<>(new Tree.Node<>(things.get(0).data.children.get(0), Tree.Node.TYPE_CONTENT), new ArrayList<>());
                    makeTree(tree, things.get(1));

                    for (final Tree<RedditObject> child : tree.getChildren()) {
                        child.generateDepths();
                    }

                    final List<Integer> depths = tree.toFlattenedDepthList();

                    treeInsetItemDecoration.setInsets(depths);
                    recyclerView.invalidateItemDecorations();

                    Observable.from(tree.toFlattenedNodeList())
                            .concatMap(node -> {
                                switch (node.getType()) {
                                    case Tree.Node.TYPE_CONTENT:
                                        return Observable.just(node)
                                                .map(Tree.Node::getData)
                                                .map(Post::new)
                                                .flatMap(Mutators.mutate())
                                                .map(p -> new Tree.Node<>(p, node.getType()));
                                    case Tree.Node.TYPE_MORE:
                                    case Tree.Node.TYPE_PROGRESS:
                                        return Observable.just(new Tree.Node<Post>(null, node.getType()));
                                    default:
                                        return Observable.error(new IllegalStateException("Unknown node type: " + node.getType()));
                                }
                            })
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ps -> {
                                nodes.addAll(ps);
                                postAdapter.notifyDataSetChanged();
                            }, Timber::e);
                }, Timber::e);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        postAdapter = new PostAdapter(getActivity(), nodes, reddit);
        treeInsetItemDecoration = new TreeInsetItemDecoration(childInsetMultiplier);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
        recyclerView.addItemDecoration(treeInsetItemDecoration);

        return recyclerView;
    }

    private static void makeTree(final Tree<RedditObject> tree, final Thing<Listing> thing) {
        for (final RedditObject childData : thing.data.children) {
            @Tree.Node.Type final int type = childData instanceof More ? Tree.Node.TYPE_MORE : Tree.Node.TYPE_CONTENT;
            final Tree<RedditObject> childTree = new Tree<>(new Tree.Node<>(childData, type), new ArrayList<>());
            tree.getChildren().add(childTree);

            if (childData instanceof Comment) {
                final Comment childComment = (Comment) childData;
                makeTree(childTree, childComment.getReplies());
            }
        }
    }
}
