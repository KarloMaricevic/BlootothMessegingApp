package com.karlomaricevic.bluetoothmessagingapp.app.di

import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.NavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    companion object {

        @Provides
        fun providesNavigatorImpl() = NavigatorImpl()
    }

    @Binds
    fun providesNavigator(navigatorImpl: NavigatorImpl) : Navigator
}
