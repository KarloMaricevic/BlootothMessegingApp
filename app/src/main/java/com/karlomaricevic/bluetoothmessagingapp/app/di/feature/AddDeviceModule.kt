package com.karlomaricevic.app2.di.feature

import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators.AddDeviceNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AddDeviceModule {

    companion object {

        @Provides
        fun providesAddDeviceNavigatorImpl(navigator: Navigator) = AddDeviceNavigatorImpl(navigator)
    }

    @Binds
    fun bindsAddDeviceNavigator(addDeviceNavigatorImpl: AddDeviceNavigatorImpl): AddDeviceNavigator
}
