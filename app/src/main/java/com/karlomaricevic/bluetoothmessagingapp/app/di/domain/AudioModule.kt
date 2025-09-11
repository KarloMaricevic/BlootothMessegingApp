package com.karlomaricevic.bluetoothmessagingapp.app.di.domain

import com.karlomaricevic.domain.audio.AudioPlayer
import com.karlomaricevic.domain.audio.AudioRepository
import com.karlomaricevic.domain.audio.DeleteAudio
import com.karlomaricevic.domain.audio.GetAudioPlayer
import com.karlomaricevic.domain.audio.GetVoiceRecorder
import com.karlomaricevic.domain.audio.VoiceRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AudioModule {

    companion object {

        @Provides
        fun providesGetAudioPlayer(player: AudioPlayer) = GetAudioPlayer(player)

        @Provides
        fun providesGetVoiceRecorder(recorder: VoiceRecorder) = GetVoiceRecorder(recorder)

        @Provides
        fun providesDeleteAudioFile(storage: AudioRepository) = DeleteAudio(storage)
    }
}