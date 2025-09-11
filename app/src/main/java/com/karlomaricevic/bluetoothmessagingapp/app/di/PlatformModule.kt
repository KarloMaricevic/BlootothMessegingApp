package com.karlomaricevic.bluetoothmessagingapp.app.di

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.platform.utils.FileStorage
import com.karlomaricevic.bluetoothmessagingapp.platform.utils.PermissionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PlatformModule {

    companion object {

        @Singleton
        @Provides
        fun providesPermissionCheckerImpl(@ApplicationContext context: Context) =
            PermissionChecker(context)

        @Singleton
        @Provides
        fun providesFileStorage(@ApplicationContext context: Context) = FileStorage(context)
    }
}
