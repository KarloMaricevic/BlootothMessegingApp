package com.karlomaricevic.bluetoothmessagingapp.domain.audio

import arrow.core.Either
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage

interface AudioPlayer {
    suspend fun setDataSource(audioUri: String): Either<ErrorMessage, Unit>
    fun play(): Either<ErrorMessage, Unit>
    fun pause(): Either<ErrorMessage, Unit>
    fun stop(): Either<ErrorMessage, Unit>
    fun release()
}
