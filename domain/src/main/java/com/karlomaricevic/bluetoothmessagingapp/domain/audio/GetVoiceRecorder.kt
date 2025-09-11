package com.karlomaricevic.bluetoothmessagingapp.domain.audio

class GetVoiceRecorder(private val voiceRecorder: VoiceRecorder) {

     operator fun invoke() = voiceRecorder
}
