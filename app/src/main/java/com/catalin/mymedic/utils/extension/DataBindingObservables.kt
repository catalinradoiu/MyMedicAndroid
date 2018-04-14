package com.catalin.mymedic.utils.extension

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField

/**
 * @author catalinradoiu
 * @since 2/13/2018
 */
inline fun ObservableBoolean.onPropertyChanged(crossinline callback: (Boolean) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback(get())
        }
    })
}

inline fun <T> ObservableField<T>.onPropertyChanged(crossinline callback: (T) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback(get())
        }
    })
}