package com.catalin.mymedic.component

import com.google.firebase.auth.FirebaseAuth

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
