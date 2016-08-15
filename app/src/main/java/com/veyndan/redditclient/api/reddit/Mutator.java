package com.veyndan.redditclient.api.reddit;

import com.veyndan.redditclient.api.reddit.model.Categories;
import com.veyndan.redditclient.api.reddit.model.Category;
import com.veyndan.redditclient.api.reddit.model.Karma;
import com.veyndan.redditclient.api.reddit.model.Thing;
import com.veyndan.redditclient.api.reddit.model.Trophies;
import com.veyndan.redditclient.api.reddit.model.Trophy;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class Mutator {

    public static final Func1<Thing<Trophies>, Observable<Trophy>> TROPHY =
            thing -> Observable.from(thing.data.trophies)
                    .map(Mutator.<Trophy>thingData());

    public static final Func1<Thing<List<Karma>>, Observable<Karma>> KARMA =
            listThing -> Observable.from(listThing.data);

    public static final Func1<Categories, Observable<String>> CATEGORY =
            categories -> Observable.from(categories.categories)
                    .map(new Func1<Category, String>() {
                        @Override
                        public String call(Category category) {
                            return category.category;
                        }
                    });

    public static <T> Func1<Thing<T>, T> thingData() {
        return thing -> thing.data;
    }
}
