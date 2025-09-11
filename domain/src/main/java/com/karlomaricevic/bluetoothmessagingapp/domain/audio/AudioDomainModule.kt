package com.karlomaricevic.bluetoothmessagingapp.domain.audio

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val audioDomainModule = DI.Module("AudioDomainModule") {
    bind<GetAudioPlayer>() with provider { GetAudioPlayer(instance()) }
    bind<GetVoiceRecorder>() with provider { GetVoiceRecorder(instance()) }
    bind<DeleteAudio>() with provider { DeleteAudio(instance()) }
}
