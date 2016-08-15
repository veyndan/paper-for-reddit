package com.veyndan.redditclient.api;

import com.veyndan.redditclient.api.model.Categories;
import com.veyndan.redditclient.api.model.Category;
import com.veyndan.redditclient.api.model.Karma;
import com.veyndan.redditclient.api.model.Thing;
import com.veyndan.redditclient.api.model.Trophies;
import com.veyndan.redditclient.api.model.Trophy;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class Mutator {

    public static final Func1<Thing<Trophies>, Observable<Trophy>> TROPHY =
            new Func1<Thing<Trophies>, Observable<Trophy>>() {
                @Override
                public Observable<Trophy> call(Thing<Trophies> thing) {
                    return Observable.from(thing.data.trophies)
                            .map(Mutator.<Trophy>thingData());
                }
            };

    public static final Func1<Thing<List<Karma>>, Observable<Karma>> KARMA =
            new Func1<Thing<List<Karma>>, Observable<Karma>>() {
                @Override
                public Observable<Karma> call(Thing<List<Karma>> listThing) {
                    return Observable.from(listThing.data);
                }
            };

    public static final Func1<Categories, Observable<String>> CATEGORY =
            new Func1<Categories, Observable<String>>() {
                @Override
                public Observable<String> call(Categories categories) {
                    return Observable.from(categories.categories)
                            .map(new Func1<Category, String>() {
                                @Override
                                public String call(Category category) {
                                    return category.category;
                                }
                            });
                }
            };

    public static <T> Func1<Thing<T>, T> thingData() {
        return new Func1<Thing<T>, T>() {
            @Override
            public T call(Thing<T> thing) {
                return thing.data;
            }
        };
    }
}
