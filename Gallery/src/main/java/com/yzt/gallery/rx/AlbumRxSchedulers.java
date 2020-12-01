package com.yzt.gallery.rx;

import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxSchedulers
 */
public class AlbumRxSchedulers {

    /**
     * 普通的线程调度器
     *
     * @param <T>：<T>
     * @return ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> normalSchedulers() {
        return upstream ->
                upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> normalSingleSchedulers() {
        return upstream ->
                upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

}