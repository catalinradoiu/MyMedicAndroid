package com.catalin.mymedic.feature.profile

/**
 * @author catalinradoiu
 * @since 6/24/2018
 */
interface PasswordChangeCallback {
    fun onSuccess()
    fun onError(errorMessage: String)
}