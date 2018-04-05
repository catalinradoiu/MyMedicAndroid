package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.User
import com.catalin.mymedic.storage.source.UsersRemoteSource
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Repository for managing the users
 *
 * @author catalinradoiu
 * @since 2/19/2018
 */

class UsersRepository @Inject constructor(private val usersRemoteSource: UsersRemoteSource) {

    /**
     * Calls the remote source to register the user
     * @return a Completable with the status of the operation
     */
    fun registerUser(user: User, password: String): Completable =
        usersRemoteSource.registerUser(user, password)
}
