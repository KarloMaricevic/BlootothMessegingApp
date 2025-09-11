package com.karlomaricevic.bluetoothmessagingapp.data.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.VoiceRecorder
import java.util.UUID

class VoiceRecorderImpl(private val context: Context) : VoiceRecorder {

    private companion object {

        const val AUDIO_EXTENSION = "3GPP"
        const val RECORDER_SAMPLE_RATE = 44100
    }

    private var mediaRecorder: MediaRecorder? = null

    override var isRecording = false
        private set

    override fun startRecording(): Either<ErrorMessage, String> {
        val fileName = UUID.randomUUID().toString() + AUDIO_EXTENSION
        return if (isRecording) {
            ErrorMessage("Recording already in progress").left()
        } else {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioSamplingRate(RECORDER_SAMPLE_RATE)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
                prepare()
                start()
                isRecording = true
            }
            fileName.right()
        }
    }

    override fun stopRecording() =
        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            isRecording = false
            Unit.right()
        } else
            ErrorMessage("Not recoding").left()

    override fun release() {
        mediaRecorder?.release()
        isRecording = false
        mediaRecorder = null
    }
}
