package com.catalin.mymedic.utils.extension

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author catalinradoiu
 * @since 2/27/2018
 */
fun Completable.mainThreadSubscribe(onComplete: Action, onError: Consumer<Throwable>) {
    this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onComplete.run()
            }, {
                onError.accept(it)
            })
}