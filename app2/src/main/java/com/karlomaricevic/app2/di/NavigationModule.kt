package com.karlomaricevic.app2.di

import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.app2.navigation.NavigatorImpl
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
