package com.karlomaricevic.bluetoothmessagingapp.data.audio

import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioPlayer
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioRepository
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.VoiceRecorder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val audioDataModule = DI.Module("AudioDataModule") {
    bind<AudioPlayer>() with singleton { AudioPlayerImpl(instance()) }
    bind<VoiceRecorder>() with singleton { VoiceRecorderImpl(instance()) }
    bind<AudioRepository>() with singleton { AudioRepositoryImpl(instance()) }
}