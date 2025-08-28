package com.karlomaricevic.domain.audio

class GetVoiceRecorder constructor(
   private val voiceRecorder: VoiceRecorder,
) {

     operator fun invoke() = voiceRecorder
}
