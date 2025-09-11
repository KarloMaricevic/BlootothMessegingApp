package com.karlomaricevic.bluetoothmessagingapp.app.di.data

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.data.audio.AudioPlayerImpl
import com.karlomaricevic.bluetoothmessagingapp.data.audio.AudioRepositoryImpl
import com.karlomaricevic.bluetoothmessagingapp.data.audio.VoiceRecorderImpl
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioPlayer
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioRepository
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.VoiceRecorder
import com.karlomaricevic.platform.utils.FileStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AudioModule {

    companion object {

        @Singleton
        @Provides
        fun providesAudioPlayerImpl(@ApplicationContext context: Context) = AudioPlayerImpl(context)

        @Singleton
        @Provides
        fun providesVoiceRecorderImpl(@ApplicationContext context: Context) = VoiceRecorderImpl(context)

        @Singleton
        @Provides
        fun providesAudioRepository(fileStorage: FileStorage) = AudioRepositoryImpl(fileStorage)
    }

    @Singleton
    @Binds
    fun bindsVoiceRecorder(recorderImpl: VoiceRecorderImpl): VoiceRecorder

    @Singleton
    @Binds
    fun bindsAudioPlayer(player: AudioPlayerImpl): AudioPlayer

    @Singleton
    @Binds
    fun bindsAudioRepository(repositoryImpl: AudioRepositoryImpl): AudioRepository
}
