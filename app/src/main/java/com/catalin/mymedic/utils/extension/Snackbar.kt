package com.catalin.mymedic.utils.extension

import android.support.design.widget.Snackbar
import android.view.View

/**
 * @author catalinradoiu
 * @since 4/5/2018
 */

fun Snackbar?.newLongSnackbar(view: View, message: String): Snackbar {
    this?.dismiss()
    return Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        show()
    }
}

fun Snackbar?.dismissIfVisible() {
    if (this?.isShown == true) {
        this.dismiss()
    }
}