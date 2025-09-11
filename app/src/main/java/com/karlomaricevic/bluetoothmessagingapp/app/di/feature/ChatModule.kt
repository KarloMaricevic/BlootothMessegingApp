package com.karlomaricevic.app2.di.feature

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators.ChatNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.ChatMessageMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.DateIndicatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.SeparatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
interface ChatModule {

    companion object {

        @Provides
        fun providesChatNavigatorImpl(navigator: Navigator) = ChatNavigatorImpl(navigator)

        @Provides
        fun providesChatMessageMapper(@ApplicationContext context: Context) = ChatMessageMapper(context)

        @Provides
        fun providesDateIndicatorMapper() = DateIndicatorMapper()

        @Provides
        fun providesSeparatorMapper() = SeparatorMapper()
    }

    @Binds
    fun providesChatNavigator(chatNavigatorImpl: ChatNavigatorImpl): ChatNavigator
}
