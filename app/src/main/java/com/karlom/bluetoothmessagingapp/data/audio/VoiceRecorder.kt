package com.karlom.bluetoothmessagingapp.data.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private companion object {
        const val RECORDER_SAMPLE_RATE = 44100
    }

    private var mediaRecorder: MediaRecorder? = null

    var isRecording = false
        private set

    fun startRecording(outUri: String) =
        if (isRecording) {
            Either.Left(Failure.ErrorMessage("Recording already in progress"))
        }
        else {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioSamplingRate(RECORDER_SAMPLE_RATE)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(outUri)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
                prepare()
                start()
                isRecording = true
            }
            Either.Right(outUri)
        }

    fun endRecording() {
        if(isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            isRecording = false
        }
    }

    fun relase() {
        mediaRecorder?.release()
        isRecording = false
        mediaRecorder = null
    }
}
