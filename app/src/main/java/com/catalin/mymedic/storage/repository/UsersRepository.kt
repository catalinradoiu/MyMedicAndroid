package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.storage.source.UsersRemoteSource
import io.reactivex.Completable
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 2/19/2018
 */

class UsersRepository @Inject constructor(private val usersRemoteSource: UsersRemoteSource) {

    fun registerUser(email: String, password: String, firstName: String, lastName: String, birthDate: Long, gender: Gender): Completable =
            usersRemoteSource.registerUser(email, password, firstName, lastName, birthDate, gender)
}
