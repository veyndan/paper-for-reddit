package com.veyndan.redditclient.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.veyndan.redditclient.Config;
import com.veyndan.redditclient.R;
import com.veyndan.redditclient.Tree;
import com.veyndan.redditclient.UserFilter;
import com.veyndan.redditclient.api.reddit.Reddit;
import com.veyndan.redditclient.api.reddit.model.Comment;
import com.veyndan.redditclient.api.reddit.model.Listing;
import com.veyndan.redditclient.api.reddit.model.More;
import com.veyndan.redditclient.api.reddit.model.RedditObject;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.post.media.mutator.Mutators;
import com.veyndan.redditclient.post.model.Post;
import com.veyndan.redditclient.ui.recyclerview.SwipeItemTouchHelperCallback;
import com.veyndan.redditclient.ui.recyclerview.itemdecoration.MarginItemDecoration;
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

public class PostsFragment extends Fragment implements PostMvpView {

    private static final String ARG_USER_FILTER = "user_filter";

    @BindDimen(R.dimen.post_child_inset_multiplier) int childInsetMultiplier;

    private final PostPresenter postPresenter = new PostPresenter();

    private RecyclerView recyclerView;

    private final List<Tree.Node<Post>> nodes = new ArrayList<>();

    private PostAdapter postAdapter;

    private LinearLayoutManager layoutManager;

    private boolean loadingPosts;

    private Reddit reddit;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(final UserFilter userFilter) {
        final PostsFragment fragment = new PostsFragment();

        final Bundle args = new Bundle();
        args.putParcelable(ARG_USER_FILTER, userFilter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        postPresenter.attachView(this);

        reddit = new Reddit.Builder(Config.REDDIT_CREDENTIALS).build();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFilter(final PostsFilter filter) {
        clearNodes();
        postPresenter.loadNodes(filter, getTrigger());
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

                    Observable.from(tree.toFlattenedNodeList())
                            .concatMap(node -> {
                                switch (node.getType()) {
                                    case Tree.Node.TYPE_CONTENT:
                                        return Observable.just(node)
                                                .map(Tree.Node::getData)
                                                .map(Post::new)
                                                .flatMap(Mutators.mutate())
                                                .map(p -> new Tree.Node<>(p, node.getType(), node.getDepth()));
                                    case Tree.Node.TYPE_MORE:
                                    case Tree.Node.TYPE_PROGRESS:
                                        return Observable.just(new Tree.Node<Post>(null, node.getType(), node.getDepth()));
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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, recyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        postAdapter = new PostAdapter(getActivity(), nodes, reddit);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MarginItemDecoration(getActivity(), R.dimen.card_view_margin));
        recyclerView.addItemDecoration(new TreeInsetItemDecoration(childInsetMultiplier));
        recyclerView.setAdapter(postAdapter);

        final ItemTouchHelper.Callback swipeCallback = new SwipeItemTouchHelperCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        final Bundle args = getArguments();
        if (args != null) {
            setFilter(args.getParcelable(ARG_USER_FILTER));
        }

        return recyclerView;
    }

    @Override
    public void onDestroy() {
        postPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void appendNode(final Tree.Node<Post> node) {
        nodes.add(node);
        postAdapter.notifyItemInserted(nodes.size() - 1);
    }

    @Override
    public void appendNodes(final List<Tree.Node<Post>> nodes) {
        final int positionStart = this.nodes.size();
        this.nodes.addAll(nodes);
        postAdapter.notifyItemRangeInserted(positionStart, nodes.size());
        loadingPosts = false;
    }

    @Override
    public Tree.Node<Post> popNode() {
        return popNode(nodes.size() - 1);
    }

    @Override
    public Tree.Node<Post> popNode(final int index) {
        final Tree.Node<Post> poppedNode = nodes.get(index);
        nodes.remove(index);
        postAdapter.notifyItemRemoved(index);
        return poppedNode;
    }

    @Override
    public void clearNodes() {
        final int nodesSize = this.nodes.size();
        this.nodes.clear();
        postAdapter.notifyItemRangeRemoved(0, nodesSize);
    }

    private Observable<Boolean> getTrigger() {
        return Observable.concat(getFirstPageTrigger(), getNextPageTrigger());
    }

    private Observable<Boolean> getFirstPageTrigger() {
        return Observable.from(nodes).count().map(integer -> integer == 1);
    }

    private Observable<Boolean> getNextPageTrigger() {
        return RxRecyclerView.scrollEvents(recyclerView)
                .filter(scrollEvent -> scrollEvent.dy() > 0) //check for scroll down
                .filter(scrollEvent -> !loadingPosts)
                .filter(scrollEvent -> {
                    final int visibleItemCount = recyclerView.getChildCount();
                    final int totalItemCount = layoutManager.getItemCount();
                    final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    return totalItemCount - visibleItemCount <= firstVisibleItem;
                })
                .map(scrollEvent -> {
                    loadingPosts = true;
                    return true;
                });
    }
}
