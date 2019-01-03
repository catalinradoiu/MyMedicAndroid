package com.catalin.mymedic.architecture

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module for providing the application context
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext() = context
}